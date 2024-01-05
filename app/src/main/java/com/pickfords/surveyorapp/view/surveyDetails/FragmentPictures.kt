package com.pickfords.surveyorapp.view.surveyDetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pickfords.surveyorapp.BuildConfig
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentPicturesBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.utils.ImagePickerDialog
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.utils.onItemClick
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.viewModel.surveyDetails.PicturesViewModel
import kotlinx.android.synthetic.main.fragment_pictures.*
import java.io.File
import java.util.*


class FragmentPictures : BaseFragment(), FragmentLifecycleInterface {
    private lateinit var picturesBinding: FragmentPicturesBinding
    private val viewModel by lazy { PicturesViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    private var imgFile: File? = null
    private var imagePath: Uri? = null
    private val cameraCode: Int = 0x50
    val galleryCode: Int = 0x51

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentPictures {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentPictures = FragmentPictures()
            fragmentPictures.arguments = bundle
            return fragmentPictures
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        picturesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pictures,
            container,
            false
        )
        return picturesBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) viewModel.init()

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        layoutCamera.setOnClickListener {
            checkImagePickerPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraCode && resultCode == Activity.RESULT_OK) {
            uploadImage()
        }
        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK) {
            imagePath = Objects.requireNonNull(data!!).data
            imgFile = File(Objects.requireNonNull(imagePath?.let {
                Utility.getPath(
                    requireActivity(),
                    it
                )
            }))
            uploadImage()
        }
    }
    // For Check Image permission
    private fun checkImagePickerPermission() {
        Dexter.withActivity(requireActivity())
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        openImagePickerDialog()
                    } else {
                        Utility.showSettingsDialog(requireActivity())
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Utility.showSettingsDialog(requireActivity())
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()
    }

    // For Check Image permission
    private fun openImagePickerDialog() {
        ImagePickerDialog(requireActivity(), object : onItemClick {
            override fun onCameraClicked() {
                displayCamera()
            }

            override fun onGalleryClicked() {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, galleryCode)
            }
        }).show()

    }

    // Display Camera Pic
    fun displayCamera() {
        val destPath: String? = Objects.requireNonNull(
            Objects.requireNonNull(requireActivity()).getExternalFilesDir(null)!!
        ).absolutePath
        val imagesFolder = File(destPath, this.resources.getString(R.string.app_name))
        try {
            imagesFolder.mkdirs()
            imgFile = File(imagesFolder, Date().time.toString() + ".jpg")
            imagePath = FileProvider.getUriForFile(
                requireActivity(),
                BuildConfig.APPLICATION_ID + ".fileProvider",
                imgFile!!
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
            startActivityForResult(intent, cameraCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // For Upload Image
    private fun uploadImage() {
        if (imgFile != null) {
            val filePath: String = imgFile!!.path
            val bitmap = BitmapFactory.decodeFile(filePath)
            viewModel.saveSurveyPicture(bitmap!!, imgFile!!.absolutePath)
        }
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        viewModel.init()
        picturesBinding.lifecycleOwner = this
        picturesBinding.viewModel = viewModel

        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            picturesBinding.data = selectedData
            viewModel.getSurveyPictureList(mContext, selectedData!!.surveyId)
            viewModel.setData(selectedData)
        }
    }
}