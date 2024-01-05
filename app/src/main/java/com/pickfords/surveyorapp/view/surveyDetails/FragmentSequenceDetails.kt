package com.pickfords.surveyorapp.view.surveyDetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSequenceDetailsBinding
import com.pickfords.surveyorapp.extentions.hideShowView
import com.pickfords.surveyorapp.extentions.hideView
import com.pickfords.surveyorapp.extentions.setTabSelection
import com.pickfords.surveyorapp.extentions.showView
import com.pickfords.surveyorapp.interfaces.DeleteFromSequence
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.CityCountryModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.SequenceDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class FragmentSequenceDetails : BaseFragment(), FragmentLifecycleInterface {

    private lateinit var sequenceDetailsBinding: FragmentSequenceDetailsBinding
    private val viewModel by lazy { SequenceDetailsViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    private var isPickford: Boolean = false
    private var isFirstTime: Boolean = true
    private var selectedPosition: Int = 0


    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sequenceDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sequence_details, container, false)
        return sequenceDetailsBinding.root
    }

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentSequenceDetails {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentSequenceDetails = FragmentSequenceDetails()
            fragmentSequenceDetails.arguments = bundle
            return fragmentSequenceDetails
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        observer()
        viewModel.selectedPositionSequence.observe(requireActivity()) {
            selectedPosition = it
            sequenceDetailsBinding.spnSelectSurvey.setSelection(selectedPosition)
            isFirstTime = false
        }

        setSpinnerTitle()

        viewModel.init(isPickford,viewLifecycleOwner)

        //  For get sequence detail live list
        viewModel.getSequenceDetailsLiveList().observe(requireActivity()) { dataList -> sequenceDetailsBinding.cbVideo.isChecked = false;
                CoroutineScope(Dispatchers.Main).launch {
                    (context as BaseActivity).runOnUiThread(Runnable {
                        try {
                            if (dataList != null && dataList.isNotEmpty()) {
                                hideView(sequenceDetailsBinding.tvNoData)
                                showView(
                                    sequenceDetailsBinding.rvSequenceDetails,
                                    sequenceDetailsBinding.cbVideo,
                                    sequenceDetailsBinding.rlSignature,
                                    sequenceDetailsBinding.btnSave,
                                    sequenceDetailsBinding.cbCarton
                                )
                                hideShowView(!sequenceDetailsBinding.cbVideo.isChecked, sequenceDetailsBinding.rlSignature)
                                sequenceDetailsBinding.rvSequenceDetails.adapter =
                                    viewModel.getSequenceDetailsAdapter()

                                if (dataList.isNotEmpty()) {
                                    if (dataList[0].surveyId != null && dataList[0].sequenceId != null) {
                                        val saveSequenceModel =
                                            (context as BaseActivity).saveSequenceDao.getSequenceBySurveyIdAndSequenceId(dataList[0].surveyId ?: "", dataList[0].sequenceId ?: "")
                                        val fromAdd: CityCountryModel =
                                            (context as BaseActivity).addressListDao.getAddressByAddressId(
                                                saveSequenceModel?.originAddressId ?: ""
                                            )
                                        val toAdd: CityCountryModel =
                                            (context as BaseActivity).addressListDao.getAddressByAddressId(
                                                saveSequenceModel?.destinationAddressId ?: ""
                                            )
                                        sequenceDetailsBinding.txtSequenceFromCity.text =
                                            if(fromAdd!=null) fromAdd.CityName+" "+fromAdd.CountryName else ""
                                        sequenceDetailsBinding.txtToCity.text =
                                            if(toAdd!=null) toAdd.CityName+" "+toAdd.CountryName else ""
                                    }

                                    sequenceDetailsBinding.llSequenceHeader.visibility = View.GONE
                                } else {
                                    sequenceDetailsBinding.llSequenceHeader.visibility = View.GONE
                                }

                            } else {
                                sequenceDetailsBinding.llSequenceHeader.visibility = View.GONE
                                showView(sequenceDetailsBinding.tvNoData)
                                hideView(
                                    sequenceDetailsBinding.rvSequenceDetails,
                                    sequenceDetailsBinding.rlSignature,
                                    sequenceDetailsBinding.cbVideo,
                                    sequenceDetailsBinding.btnSave
                                )
                            }
                        }catch (e: Exception){
                        }
                    })

                }
        }



        viewModel.getSignatureData.observe(requireActivity()) { jsonObject ->
            if (!jsonObject.data.isNullOrEmpty()) //
                CoroutineScope(Dispatchers.Main).launch {// sequenceDetailsBinding.signaturePad.clear()
                    try {
                        sequenceDetailsBinding.signaturePad.signatureBitmap =
                            baseToBitmap(jsonObject.data ?: "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

        viewModel.getSignatureDataOffline.observe(requireActivity()) { jsonObject ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!jsonObject.isNullOrEmpty()) {
                    try {
                        sequenceDetailsBinding.signaturePad.signatureBitmap =
                            baseToBitmap(jsonObject)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    sequenceDetailsBinding.signaturePad.clear()
                    Log.e("Signature", "Blank")
                }
            }
        }

        viewModel.getIsVideoDataOffline.observe(requireActivity()) { checked ->
            CoroutineScope(Dispatchers.Main).launch {
                sequenceDetailsBinding.cbVideo.isChecked = checked
            }
        }
        
    }


    //  Set Tittle For Spinner
    private fun setSpinnerTitle() {
        sequenceDetailsBinding.spnSelectSurvey.setTitle("");
    }

    private fun observer() {
        viewModel.saveSignatureData.observe(requireActivity()) {}
    }


    private var onDeleteFromSequence = object : DeleteFromSequence {
        override fun onLongClickSelectedItem(model: SequenceDetailsModel?, position: Int?) {
            model?.let { viewModel.deleteSequenceInventoryItem(it,position) }
        }

        override fun onSelectedItem(name: String?, model: SequenceDetailsModel?) {

        }

        override fun onEditSelectedItem(model: SequenceDetailsModel?, position: Int?) {

        }

        override fun onChangeSelectedItem(model: SequenceDetailsModel?, position: Int?) {
            (context as BaseActivity).sequenceDetailsDao.update(model)
        }
    }

        private fun onClick() {
        sequenceDetailsBinding.txtPickfords.setOnClickListener {
            if (!isPickford) {
                isPickford = true
                hideView(sequenceDetailsBinding.rlSignature)
                sequenceDetailsBinding.signaturePad.clear()
                setTabSelection(
                    sequenceDetailsBinding.txtPickfords,
                    sequenceDetailsBinding.txtCustomer
                )
                onResumeFragment(null)
            }
        }

        sequenceDetailsBinding.txtCustomer.setOnClickListener { selectedPosition = sequenceDetailsBinding.spnSelectSurvey.selectedItemPosition;

            if (isPickford) {
                isPickford = false
                showView(sequenceDetailsBinding.rlSignature)
                setTabSelection(
                    sequenceDetailsBinding.txtCustomer,
                    sequenceDetailsBinding.txtPickfords
                )
                onResumeFragment(null)
            }
            sequenceDetailsBinding.spnSelectSurvey.setSelection(selectedPosition)
        }

        sequenceDetailsBinding.btnSave.setOnClickListener {

            if (AppConstants.revisitPlanning){
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
            }else{
                if (!sequenceDetailsBinding.cbVideo.isChecked && sequenceDetailsBinding.signaturePad.isEmpty)
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.please_enter_signature),
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    val signature = if (sequenceDetailsBinding.cbVideo.isChecked) "" else bitmapToBaseString()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("SurveyId", selectedData?.surveyId)
                    jsonObject.addProperty("IsVideoSurvey", sequenceDetailsBinding.cbVideo.isChecked)
                    jsonObject.addProperty("Signature", signature)
                    viewModel.saveSignature(jsonObject, signature)
                }
            }


        }

        sequenceDetailsBinding.imgClear.setOnClickListener {
            sequenceDetailsBinding.signaturePad.clear()
        }

        sequenceDetailsBinding.cbVideo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                sequenceDetailsBinding.signaturePad.clear()
            }
            hideShowView(!b, sequenceDetailsBinding.rlSignature)
        }

        sequenceDetailsBinding.cbCarton.setOnCheckedChangeListener { compoundButton, b ->
           if (compoundButton.isChecked){
               viewModel.flagCartoon = true
               viewModel.getCartoonList(requireActivity(), AppConstants.surveyID, selectedData!!.surveyId)
           }
         else{
               viewModel.flagCartoon = false
               viewModel.getSurveyDetailList(requireActivity(), selectedData!!.surveyId)
         }
        }
    }

    override fun onPauseFragment() {
    }


    override fun onResumeFragment(s: String?) {

        viewModel.setDeleteInventory(onDeleteFromSequence)
        sequenceDetailsBinding.cbCarton.isChecked = false

        if (sequenceDetailsBinding.viewModel == null) {
            sequenceDetailsBinding.lifecycleOwner = this
            sequenceDetailsBinding.viewModel = viewModel
        }

        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
                viewModel.getSurveyDetailList(requireActivity(), selectedData!!.surveyId)
            }
        }

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        // Logic for Is user First Time
        if (!isFirstTime) {
            viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
            sequenceDetailsBinding.spnSelectSurvey.post {
                sequenceDetailsBinding.spnSelectSurvey.setSelection(selectedPosition)
            }
        } else {
            viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
        }

        // For select sequence code
        if((context as DashboardActivity).setSelectedSequencePosition() > 0){
            var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition())
            viewModel.selectedPosition.postValue(selectedPosition)
            viewModel.selectedPositionSequence.postValue(selectedPosition)
        }

    }

    // For signature to Bitmap
    private fun baseToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    // For bitmap to String
    private fun bitmapToBaseString(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        sequenceDetailsBinding.signaturePad.signatureBitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            byteArrayOutputStream
        )
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}