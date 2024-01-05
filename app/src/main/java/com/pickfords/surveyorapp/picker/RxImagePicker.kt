package com.pickfords.surveyorapp.picker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class RxImagePicker : Fragment() {

    private lateinit var attachedSubject: PublishSubject<Boolean>
    private lateinit var publishSubject: PublishSubject<Uri>
    private lateinit var publishSubjectMultipleImages: PublishSubject<List<Uri>>
    private lateinit var canceledSubject: PublishSubject<Int>

    private var allowMultipleImages = false
    private var imageSource: Sources? = null
    private var chooserTitle: String? = null

    fun requestImage(source: Sources, chooserTitle: String?): Observable<Uri> {
        this.chooserTitle = chooserTitle
        return requestImage(source)
    }

    fun requestImage(source: Sources): Observable<Uri> {
        initSubjects()
        allowMultipleImages = false
        imageSource = source
        requestPickImage()
        return publishSubject.takeUntil(canceledSubject)
    }

    fun requestMultipleImages(): Observable<List<Uri>> {
        initSubjects()
        imageSource = Sources.GALLERY
        allowMultipleImages = true
        requestPickImage()
        return publishSubjectMultipleImages.takeUntil(canceledSubject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private fun initSubjects() {
        publishSubject = PublishSubject.create()
        attachedSubject = PublishSubject.create()
        canceledSubject = PublishSubject.create()
        publishSubjectMultipleImages = PublishSubject.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (::attachedSubject.isInitialized.not() or
            ::publishSubject.isInitialized.not() or
            ::publishSubjectMultipleImages.isInitialized.not() or
            ::canceledSubject.isInitialized.not()
        ) {
            initSubjects()
        }
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SELECT_PHOTO -> handleGalleryResult(data)
                TAKE_PHOTO -> onImagePicked(cameraPictureUrl)
                CHOOSER -> if (isPhoto(data)) {
                    onImagePicked(cameraPictureUrl)
                } else {
                    handleGalleryResult(data)
                }
            }
        } else {
            canceledSubject.onNext(requestCode)
        }
    }

    private fun isPhoto(data: Intent?): Boolean {
        return data == null || data.data == null && data.clipData == null
    }

    private fun handleGalleryResult(data: Intent?) {
        if (allowMultipleImages) {
            val imageUris = ArrayList<Uri>()
            val clipData = data!!.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } else {
                imageUris.add(data.data!!)
            }
            onImagesPicked(imageUris)
        } else {
            onImagePicked(data!!.data)
        }
    }

    @SuppressLint("CheckResult")
    private fun requestPickImage() {
        if (!isAdded) {
            attachedSubject.subscribe { pickImage() }
        } else {
            pickImage()
        }
    }

    private fun pickImage() {
        if (!checkPermission()) {
            return
        }

        var chooseCode = 0
        var pictureChooseIntent: Intent? = null

        when (imageSource) {
            Sources.CAMERA -> {
                cameraPictureUrl = createImageUri()
                pictureChooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)
                grantWritePermission(requireContext(), pictureChooseIntent, cameraPictureUrl!!)
                chooseCode = TAKE_PHOTO
            }
            Sources.GALLERY -> {
                pictureChooseIntent = createPickFromGalleryIntent()
                chooseCode = SELECT_PHOTO
            }
            Sources.DOCUMENTS -> {
                pictureChooseIntent = createPickFromDocumentsIntent()
                chooseCode = SELECT_PHOTO
            }
            Sources.CHOOSER -> {
                pictureChooseIntent = createChooserIntent(chooserTitle)
                chooseCode = CHOOSER
            }
            else -> {}
        }

        startActivityForResult(pictureChooseIntent, chooseCode)
    }

    private fun createChooserIntent(chooserTitle: String?): Intent {
        cameraPictureUrl = createImageUri()
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = requireContext().packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)
            grantWritePermission(requireContext(), intent, cameraPictureUrl!!)
            cameraIntents.add(intent)
        }
        val galleryIntent = createPickFromDocumentsIntent()
        val chooserIntent = Intent.createChooser(galleryIntent, chooserTitle)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        return chooserIntent
    }

    private fun createPickFromGalleryIntent(): Intent {
        var pictureChooseIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleImages)
        }
        return pictureChooseIntent
    }

    private fun createPickFromDocumentsIntent(): Intent {
        val pictureChooseIntent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleImages)
            pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        } else {
            pictureChooseIntent = Intent(Intent.ACTION_GET_CONTENT)
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pictureChooseIntent.type = "image/*"
        return pictureChooseIntent
    }

    private fun checkPermission(): Boolean {


        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), 0
                )
            }
            false
        } else {
            true
        }
    }

    private fun createImageUri(): Uri? {
        val contentResolver = requireActivity().contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun onImagesPicked(uris: List<Uri>) {
        publishSubjectMultipleImages.onNext(uris)
        publishSubjectMultipleImages.onComplete()
    }

    private fun onImagePicked(uri: Uri?) {
        publishSubject.onNext(uri!!)
        publishSubject.onComplete()
    }

    companion object {

        private const val SELECT_PHOTO = 100
        private const val TAKE_PHOTO = 101
        private const val CHOOSER = 102

        private val TAG = RxImagePicker::class.java.simpleName
        private var cameraPictureUrl: Uri? = null

        fun with(fragmentManager: FragmentManager): RxImagePicker {
            var rxImagePickerFragment = fragmentManager.findFragmentByTag(TAG) as RxImagePicker?
            if (rxImagePickerFragment == null) {
                rxImagePickerFragment = RxImagePicker()
                fragmentManager.beginTransaction()
                    .add(rxImagePickerFragment, TAG)
                    .commit()
            }
            return rxImagePickerFragment
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public fun getPath(context: Context, uri: Uri): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    /*final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);*/if (id != null && id.startsWith(
                            "raw:"
                        )
                    ) {
                        return id.substring(4)
                    }
                    val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                    )
                    for (contentUriPrefix in contentUriPrefixesToTry) {
                        try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                java.lang.Long.valueOf(id)
                            )
                            val path: String? =
                                getDataColumn(context, contentUri, null, null)
                            if (path != null) {
                                return path
                            }
                        } catch (ignored: Exception) {
                        }
                    }

                    // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    val fileName = getFileName(context, uri)
                    val cacheDir = getDocumentCacheDir(context)
                    val file = generateFileName(fileName, cacheDir)
                    var destinationPath: String? = null
                    if (file != null) {
                        destinationPath = file.absolutePath
                        saveFileFromUri(context, uri, destinationPath)
                    }
                    return destinationPath
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }
        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
            var `is`: InputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                `is` = context.contentResolver.openInputStream(uri)
                bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
                val buf = ByteArray(1024)
                `is`!!.read(buf)
                do {
                    bos.write(buf)
                } while (`is`.read(buf) != -1)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                    bos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun generateFileName(name: String?, directory: File?): File? {
            var name = name ?: return null
            var file = File(directory, name)
            if (file.exists()) {
                var fileName = name
                var extension = ""
                val dotIndex = name.lastIndexOf('.')
                if (dotIndex > 0) {
                    fileName = name.substring(0, dotIndex)
                    extension = name.substring(dotIndex)
                }
                var index = 0
                while (file.exists()) {
                    index++
                    name = "$fileName($index)$extension"
                    file = File(directory, name)
                }
            }
            try {
                if (!file.createNewFile()) {
                    return null
                }
            } catch (e: IOException) {
                //Log.w(TAG, e);
                return null
            }

            //logDir(directory);
            return file
        }

        fun getDocumentCacheDir(context: Context): File {
            val dir = File(context.cacheDir, DOCUMENTS_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            //        logDir(context.getCacheDir());
            //        logDir(dir);
            return dir
        }

        val DOCUMENTS_DIR = "documents"

        fun getName(filename: String?): String? {
            if (filename == null) {
                return null
            }
            val index = filename.lastIndexOf('/')
            return filename.substring(index + 1)
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun getFileName(context: Context, uri: Uri): String? {
            val mimeType = context.contentResolver.getType(uri)
            var filename: String? = null
            if (mimeType == null && context != null) {
                val path = getPath(context, uri)
                filename = if (path == null) {
                    getName(uri.toString())
                } else {
                    val file = File(path)
                    file.name
                }
            } else {
                val returnCursor = context.contentResolver.query(uri, null, null, null, null)
                if (returnCursor != null) {
                    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    returnCursor.moveToFirst()
                    filename = returnCursor.getString(nameIndex)
                    returnCursor.close()
                }
            }
            return filename
        }
    }

}
