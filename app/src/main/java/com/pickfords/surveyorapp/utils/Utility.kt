package com.pickfords.surveyorapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Base64
import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.model.surveyDetails.SubmitDataModel
import com.pickfords.surveyorapp.model.surveyDetails.SyncDataModel
import com.pickfords.surveyorapp.utils.AppConstants.baseURL
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.DataSavedDialog
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utility {

    companion object {
        private const val requestCode: Int = 1111

        private val VALID_EMAIL_ADDRESS_REGEX: Pattern =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        private val VALID_PASSWORD_REGEX: Pattern = Pattern.compile(
            "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$",
            Pattern.CASE_INSENSITIVE
        )

        fun getCurrentDate() : String{
            var currentDate : String = "";
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            currentDate = LocalDateTime.now().format(formatter)
           return currentDate;
        }

        fun changeBaseURL(int_URL: Int) {
            // Staging URL
            baseURL =  if (int_URL == 0) {
                // Staging URL
                  AppConstants.StagingURL
                 }
                 // Live URL
                 else if (int_URL == 1){
                    AppConstants.LiveURL
                 }
                else if (int_URL == 2) {
                    AppConstants.TestLiveURL
                }

            else  {
                // Local URL
                "http://192.168.1.103:10111/api/"
            }
        }

        @SuppressLint("ObsoleteSdkInt")
        fun getDrawable(context: Context?, id: Int): Drawable {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ContextCompat.getDrawable(context!!, id)!!
            } else {
                ResourcesCompat.getDrawable(context!!.resources, id, null)!!
            }
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }


        private fun getDataColumn(
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
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } finally {
                cursor?.close()
            }
            return null
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
                    if (id != null && id.startsWith(
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

        private fun isMediaDocument(uri: Uri): Boolean {
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

        private fun generateFileName(nameS: String?, directory: File?): File? {
            var name = nameS ?: return null
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
                return null
            }
            return file
        }

        private fun getDocumentCacheDir(context: Context): File {
            val dir = File(context.cacheDir, "documents")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

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

       /**
         * Check Internet is connected or not
         */
        fun isNetworkConnected(context: Context?): Boolean {
            val connectivityManager: ConnectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT < 23) {
                val ni = connectivityManager.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI
                            || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val network: Network? = connectivityManager.activeNetwork
                if (network != null) {
                    val networkCapabilities: NetworkCapabilities? =
                        connectivityManager.getNetworkCapabilities(network)

                    return networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
            return false
        }

        /**
         * Share string Message To Other Apps
         */
        fun shareMessageToOtherApps(mContext: Context, message: String, extraMessage: String) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, extraMessage)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        fun isEmailValid(string: String): Boolean {
            val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(string)
            return matcher.find()
        }

       fun formatDecimal(doubleValue: Double?): String? {
            val numberFormat: NumberFormat = DecimalFormat("##.##")
            return "$" + numberFormat.format(doubleValue)
        }

        fun copyUriToFile(context: Context, uri: Uri): File? {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            var outFile: File? = null
            try {
                if (context.contentResolver != null) {
                    `in` = context.contentResolver.openInputStream(uri)
                    val path: String = "temp_image_2_" + System.currentTimeMillis() + ".jpg"
                    outFile = createImageFile(context, path)
                    if (outFile != null && `in` != null) {
                        out = FileOutputStream(outFile)
                        val buf = ByteArray(1024)
                        var len: Int
                        while (`in`.read(buf).also { len = it } > 0) {
                            out.write(buf, 0, len)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally { // Ensure that the InputStreams are closed even if there's an exception.
                try {
                    out?.close()
                    // If you want to close the "in" InputStream yourself then remove this
                    // from here but ensure that you close it yourself eventually.
                    `in`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return outFile
        }

        @Throws(IOException::class)
        fun createImageFile(context: Context, imageFileName: String): File? {
            var storageDir = context.filesDir
            val dirCreated: Boolean
            if (storageDir == null) {
                val externalStorage =
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                if (externalStorage == null) {
                    storageDir =
                        File(context.cacheDir, Environment.DIRECTORY_PICTURES)
                    dirCreated = storageDir.exists() || storageDir.mkdirs()
                } else {
                    dirCreated = true
                }
            } else {
                storageDir =
                    File(context.filesDir, Environment.DIRECTORY_PICTURES)
                dirCreated = storageDir.exists() || storageDir.mkdirs()
            }
            return if (dirCreated) {
                val imageFile = File(storageDir, imageFileName)
                var isDeleted = true
                if (imageFile.exists()) {
                    isDeleted = imageFile.delete()
                }
                if (isDeleted) {
                    val fileCreated = imageFile.createNewFile()
                    if (fileCreated) imageFile else null
                } else {
                    null
                }
            } else {
                null
            }
        }

        fun showSettingsDialog(context: Activity?) {
            val builder =
                MaterialAlertDialogBuilder(context!!, R.style.AlertDialogTheme)
            builder.setTitle("Need Permissions")
            builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                dialog!!.dismiss()
                openSettings(context)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog!!.dismiss()
            }
            builder.show()
        }

        // navigating user to app settings
        private fun openSettings(context: Activity?) {
            val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context?.packageName, null)
            intent.data = uri
            context?.startActivityForResult(intent, requestCode)
        }

        fun isNull(str: String?): Boolean {
            return str == null || str.trim() == "" || str.trim().isEmpty()
        }

        fun getTodayDate(): String {
            val date: String =
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            return date
        }
        fun getRandomNumber(): String {
            val randomPIN = (Math.random() * 9000).toInt() + 1000
            return randomPIN.toString()
        }

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun showDaysLeft(tvDaysLeft: TextView, secondDueDate: String?) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd") // H:mm aa
            if (secondDueDate != null && secondDueDate.isNotEmpty()) {
                tvDaysLeft.visibility = VISIBLE
                val millionSeconds =
                    simpleDateFormat.parse(secondDueDate)?.time!! - Calendar.getInstance().timeInMillis
                val days = TimeUnit.MILLISECONDS.toDays(millionSeconds)
                if (days <= 0) {
                    tvDaysLeft.text = ""
                } else {
                    val left = if (days > 1) " days left" else " day left"
                    tvDaysLeft.text =
                        days.toString() + left
                }
            } else {
                tvDaysLeft.visibility = GONE
            }

        }
        private fun isValidMobile(phone: String): Boolean {
            return Patterns.PHONE.matcher(phone).matches()
        }

        fun callToNumber(context: Activity, phoneNo: String?) {
            if (phoneNo != null && isValidMobile(phoneNo)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val dialIntent = Intent(Intent.ACTION_CALL)
                    dialIntent.data = Uri.parse("tel:$phoneNo")
                    context.startActivity(dialIntent)
                } else {
                    ActivityCompat.requestPermissions(
                        context, arrayOf(Manifest.permission.CALL_PHONE),
                        0
                    )
                }
            } else {
//                Toast.displayError(context, "Phone No is not valid")
            }
        }

       private fun getFolderSize(file: File): Long {
            var size: Long = 0
            if (file.isDirectory) {
                for (child in file.listFiles()) {
                    size += getFolderSize(child)
                }
            } else {
                size = file.length()
            }
            return size
        }

        fun getLastName(lastname: String?): String {
            if (lastname != null && lastname.isNotEmpty()) {
                return " $lastname"
            }
            return ""
        }

        fun checkGreaterDates(firstDate: String, secondDate: String): Boolean {
            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
            return simpleDateFormat.parse(firstDate).after(simpleDateFormat.parse(secondDate))
        }

        fun checkGreaterDates(firstDate: Date, secondDate: Date): Boolean {
            return firstDate.after(secondDate)
        }

        fun setOfflineTag(context: Context, message: String?) {
            val model = SyncDataModel("0", true)
            (context as BaseActivity).syncDao.insert(model)
            if (message != null) {
                DataSavedDialog(context, message).show()
            }
            //SyncDataDialog(this, "")
        }

        fun submitSurvey(context: Context, surveyId: String) {
            val model = SubmitDataModel("0", surveyId)
            (context as BaseActivity).submitDao.insert(model)
        }

        fun getMeasurementType(measurement: String, context: Context): Int {
            return when (measurement) {
                context.getString(R.string.inches) -> 1
                context.getString(R.string.feet) -> 2
                context.getString(R.string.centimeters) -> 3
                context.getString(R.string.metres) -> 4
                else -> 0
            }
        }

        fun getMeasurementTypeName(measurement: String, context: Context): String {
            return when (measurement) {
                "1" -> context.getString(R.string.inches)
                "2" -> context.getString(R.string.feet)
                "3" -> context.getString(R.string.centimeters)
                "4" -> context.getString(R.string.metres)
                else -> ""
            }
        }

        fun convertFeetToInch(editText: AppCompatEditText) {
            if (!editText.text.isNullOrEmpty()) {
                editText.setText((editText.text.toString().toInt() * 12).toString())
            }
        }

        fun convertInchToFeet(editText: AppCompatEditText) {
            if (!editText.text.isNullOrEmpty()) {
                editText.setText((editText.text.toString().toInt() * 0.0833333).toString())
            }
        }

        fun convertMeterToCentimeter(editText: AppCompatEditText) {
            if (!editText.text.isNullOrEmpty()) {
                editText.setText((editText.text.toString().toInt() * 100).toString())
            }
        }

        fun convertCentimeterToMeter(editText: AppCompatEditText) {
            if (!editText.text.isNullOrEmpty()) {
                editText.setText((editText.text.toString().toInt() * 0.01).toString())
            }
        }

        fun getNextSequenceNumber(sequence: String?): String {
            if (!sequence.isNullOrEmpty()) {
                try {
                    val lastTwoChar = sequence.takeLast(2)
                    var nextSequence = sequence.removeSuffix(lastTwoChar)
                    if (isNumeric(lastTwoChar)) {
                        nextSequence += if (lastTwoChar == "99") {
                            "A0"
                        } else {
                            (lastTwoChar.toInt() + 1).toString()
                        }
                    } else {
                        val lastChar = lastTwoChar.takeLast(1)
                        nextSequence += if (lastChar == "9") {
                            var firstChar: Char = lastTwoChar.first()
                            ++firstChar + "0"
                        } else {
                            lastTwoChar.first() + (lastChar.toInt() + 1).toString()
                        }
                    }
                    return nextSequence
                } catch (e: Exception) {
                    return "00"
                }
            } else {
                return "00"
            }
        }

        private fun isNumeric(toCheck: String): Boolean {
            return toCheck.all { char -> char.isDigit() }
        }

        fun resizeBase64Image(base64image: String): String? {
            val encodeByte = Base64.decode(base64image.toByteArray(), Base64.DEFAULT)
            val options = BitmapFactory.Options()
            options.inPurgeable = true
            var image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size, options)
            if (image.height <= 400 && image.width <= 400) {
                return base64image
            }
            image = Bitmap.createScaledBitmap(image, 100, 100, false)
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            System.gc()
            return Base64.encodeToString(b, Base64.NO_WRAP)
        }
    }

}