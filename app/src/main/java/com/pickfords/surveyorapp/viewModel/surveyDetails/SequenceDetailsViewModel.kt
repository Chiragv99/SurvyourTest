package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.interfaces.DeleteFromSequence
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.surveyDetails.SequenceDetailsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SequenceDetailsViewModel(val context: Context) : BaseViewModel() {

    private var sequenceDetailsDataList: ArrayList<SequenceDetailsModel> =
        ArrayList()
    private var sequenceDetailsLiveList: MutableLiveData<List<SequenceDetailsModel>> =
        MutableLiveData()
    private var sequenceDetailList = ArrayList<SequenceDetailViewTypeModel>()
    var saveSignatureData: MutableLiveData<BaseModel<Any>> = MutableLiveData()
    var getSignatureData: MutableLiveData<BaseModel<String>> = MutableLiveData()
    var getSignatureDataOffline: MutableLiveData<String> = MutableLiveData()
    var getIsVideoDataOffline: MutableLiveData<Boolean> = MutableLiveData()
    private var sequenceDetailsAdapter: SequenceDetailsAdapter? = null

    fun getSequenceDetailsAdapter(): SequenceDetailsAdapter? = sequenceDetailsAdapter
    fun getSequenceDetailsLiveList(): MutableLiveData<List<SequenceDetailsModel>> =
        sequenceDetailsLiveList

    private var surveyId: String? = null
    private var surveySequenceId: String = "0"
    private var surveySequenceName: String? = ""
    var selectedPosition: MutableLiveData<Int> = MutableLiveData()
    var selectedItemPosition: Int = 0
    var selectedPositionSequence: MutableLiveData<Int> = MutableLiveData()


    val sequenceList: MutableList<String?> = mutableListOf()
    internal var sequenceLiveList: MutableLiveData<List<SaveSequenceModel>> = MutableLiveData()
    private var sequenceSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceSpinnerAdapter(): ArrayAdapter<String?>? = sequenceSpinnerAdapter
    // Return Distance Type Unit API Response

    private var onDeleteFromSequence: DeleteFromSequence? = null


    // Flag for show cartoon items
    var flagCartoon : Boolean = false
    var flagSelection = false

    // For Check Is Video Survey or Not
    var isVideoSurvey : Boolean = false
    @SuppressLint("NotifyDataSetChanged")
    fun init(isPickford: Boolean, viewLifecycleOwner: LifecycleOwner) {
         flagCartoon = false
        sequenceDetailsLiveList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                sequenceDetailsDataList.clear()
                sequenceDetailsDataList.addAll(it)
                val groupBy: Map<String, List<SequenceDetailsModel>> = sequenceDetailsDataList.groupBy { it.surveySequenceLegId ?: "UnKnown" }
                val entries = groupBy.entries
                sequenceDetailList = ArrayList<SequenceDetailViewTypeModel>()

                CoroutineScope(Dispatchers.Main).launch {
                    entries.forEach {
                        val sequenceDetailHeader = it.value.firstOrNull()
                        if (sequenceDetailHeader != null) {
                            if(sequenceDetailHeader.surveySequenceLeg!=null && sequenceDetailHeader.surveySequenceLeg!!.isNotEmpty()){
                                try {
                                    if (sequenceDetailHeader.surveySequenceLegId != null) {
                                        val showLegsModel = (context as BaseActivity).showLegsDao.getLegBySurveySequenceLegId(sequenceDetailHeader.surveySequenceLegId?:"")
                                        val fromAdd: CityCountryModel = (context as BaseActivity).addressListDao.getAddressByAddressId(showLegsModel?.originAddressId?:"")
                                        val toAdd: CityCountryModel = (context as BaseActivity).addressListDao.getAddressByAddressId(showLegsModel?.destinationAddressId?:"")
                                        sequenceDetailList.add(
                                            SectionHeaderSeqaunceDetails(
                                                if(fromAdd!=null) fromAdd.CityName+" "+fromAdd.CountryName else "",
                                                if(toAdd!=null) toAdd.CityName+" "+toAdd.CountryName else "",
                                                sequenceDetailHeader.surveySequenceLeg ?: ""
                                            )
                                        )
                                    }
                                }catch (e: Exception){
                                }
                            } else {
                                try {
                                    if (sequenceDetailHeader.surveyId != null && sequenceDetailHeader.sequenceId != null) {
                                        val saveSequenceModel = (context as BaseActivity).saveSequenceDao.getSequenceBySurveyIdAndSequenceId(sequenceDetailHeader.surveyId?:"", sequenceDetailHeader.sequenceId?:"")
                                        val fromAdd: CityCountryModel = (context as BaseActivity).addressListDao.getAddressByAddressId(saveSequenceModel.originAddressId?:"")
                                        val toAdd: CityCountryModel = (context as BaseActivity).addressListDao.getAddressByAddressId(saveSequenceModel.destinationAddressId?:"")
                                        sequenceDetailList.add(
                                            SectionHeaderSeqaunceDetails(
                                                if(fromAdd!=null) fromAdd.CityName+" "+fromAdd.CountryName else "",
                                                if(toAdd!=null) toAdd.CityName+" "+toAdd.CountryName else "",
                                                sequenceDetailHeader.surveySequenceLeg ?: ""
                                            )
                                        )
                                    }
                                }catch (e: Exception){
                                }

                            }
                        }
                        sequenceDetailList.addAll(it.value)
                    }
                }
            }
            sequenceDetailsAdapter = SequenceDetailsAdapter(sequenceDetailList, isPickford, sequenceDetailsDataList,onDeleteFromSequence)
            sequenceDetailsAdapter?.notifyDataSetChanged()
        })


        sequenceSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceList)
        sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)


            sequenceSpinnerAdapter =
                ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceList)
            sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

            sequenceLiveList.observeForever {
                if (it != null) {
                    sequenceList.clear()
                    sequenceList.add("Select Sequence")
                    for (data in it) {
                        val name =
                            if (data.surveySequence == null) data.labelToUse else data.surveySequence
                        sequenceList.add(name)
                    }
                    sequenceSpinnerAdapter?.notifyDataSetChanged()
                }
            }
    }


    fun setDeleteInventory(onDeleteFromSequence: DeleteFromSequence) {
        this.onDeleteFromSequence = onDeleteFromSequence
    }

    fun deleteSequenceInventoryItem(sequenceDetailsModel: SequenceDetailsModel, position: Int?) {
        if (sequenceDetailsModel.surveyInventoryId.contains("ADD")) {
            (context as BaseActivity).sequenceDetailsDao.delete(sequenceDetailsModel.surveyInventoryId)
        } else {
            (context as BaseActivity).sequenceDetailsDao.updateIsDelete(
                sequenceDetailsModel.surveyInventoryId,
                true
            )
        }
        if (position!! > 1){
            sequenceDetailList.remove(sequenceDetailsModel)
            sequenceDetailsDataList.remove(sequenceDetailsModel)
            sequenceDetailsAdapter?.notifyDataSetChanged()
        }else{
            getSurveyDetailList(context, surveyId!!)
        }
        val sequenceDetailsList: List<SequenceDetailsModel> =
            (context as BaseActivity).sequenceDetailsDao.getSurveyDetailsBySurveyIdAndSequenceId(surveyId.toString(), surveySequenceId)

        if (sequenceDetailsList.isEmpty()){
            context.sequenceDetailSignatureDao.deleteBySequenceId(surveyId.toString(), surveySequenceId)
        }
    }


    //  For Click Listener Sequence
    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (sequenceLiveList != null && sequenceLiveList.value != null && sequenceLiveList.value!!.size >= position && position != 0) {
                surveySequenceId = sequenceLiveList.value!![position - 1].surveySequenceId
                surveySequenceName= sequenceLiveList.value!![position - 1].surveySequence
                selectedPosition.value = position
                selectedItemPosition = position
                selectedPositionSequence.postValue(position)
                AppConstants.revisitPlanning =  sequenceLiveList.value!![position - 1].isRevisit
                flagSelection = false
                if (!flagCartoon){
                    getSurveyDetailList(context, surveyId!!)
                }
                else{
                    getCartoonList(context, surveyId!!, surveyId!!)
                }
            } else {
                surveySequenceId = "0"
                selectedItemPosition = 0
                selectedPositionSequence.postValue(0)
            }
        }
    }

    //  For Set Survey Id
    fun setSurveyId(id: String?, position: Int, isFirstCall: Boolean, isSavedCall: Boolean) {
        surveyId = id
        if ((context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).isNotEmpty()) {
            sequenceLiveList.postValue(
                (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(
                    surveyId.toString(),false
                )
            )

            if (isFirstCall) {
                selectedPositionSequence.postValue(1)
            }
        }
    }

    //  For Get Survey Detail List
    fun getSurveyDetailList(context: Context, surveyId: String) {
        class GetSurveyDetail : AsyncTask<Void, Void, Void?>() {
            override fun onPreExecute() {
            }

            override fun doInBackground(vararg p0: Void?): Void? {
                if ((context as BaseActivity).sequenceDetailsDao.getSurveyDetailsBySurveyId(surveyId, surveySequenceId!!).isNotEmpty()) {
                    sequenceDetailsLiveList.postValue(null)
                    sequenceDetailsLiveList.postValue(context.sequenceDetailsDao.getSurveyDetailsBySurveyId(surveyId, surveySequenceId!!))

                    if ((context as BaseActivity).sequenceDetailSignatureDao.getSignature() != null && (context as BaseActivity).sequenceDetailSignatureDao.getSignatureById(surveyId,surveySequenceId) != null) {
                        if (!context.sequenceDetailSignatureDao.getSignatureById(surveyId,surveySequenceId)!!.signature.equals("")){
                            getSignatureDataOffline.postValue(context.sequenceDetailSignatureDao.getSignatureById(surveyId,surveySequenceId)!!.signature)
                        }else{
                            getSignatureDataOffline.postValue(null)
                        }
                        getIsVideoDataOffline.postValue(context.sequenceDetailSignatureDao.getSignatureById(surveyId,surveySequenceId).isVideoSequence)
                    }else{
                        getSignatureDataOffline.postValue(null)
                    }
                } else {
                    sequenceDetailsLiveList.postValue(null)
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                // hideProgressbar()
            }

        }
        GetSurveyDetail().execute()
    }

    fun  getCartoonList(context: Context, surveyId: String, sequenceId: String){
        Log.e("Cartoon",AppConstants.cartoonId + " "+surveyId + " "+surveySequenceId)
        if (!(context as BaseActivity).sequenceDetailsDao.getCartoonItems(AppConstants.cartoonId,surveyId,surveySequenceId.toString()).isNullOrEmpty()) {
            sequenceDetailsLiveList.postValue((context as BaseActivity).sequenceDetailsDao.getCartoonItems(AppConstants.cartoonId,surveyId, surveySequenceId.toString()))
        }else{
            sequenceDetailsLiveList.postValue(null)
        }
    }


    //  For Save Customer Signature API
    fun saveSignature(jsonObject: JsonObject, bitmapToBaseString: String) {
        val sequence = SeqeunceDetailSignature()
        sequence.signature = if (jsonObject.get("IsVideoSurvey")!!.asBoolean) "" else bitmapToBaseString!!
        sequence.surveyId = jsonObject.get("SurveyId")!!.asInt
        sequence.isVideoSequence = jsonObject.get("IsVideoSurvey")!!.asBoolean
        sequence.isOffline = true
        sequence.surveySequenceId = surveySequenceId!!.toString()
        sequence.sequenceNo = surveySequenceName!!.toString()
        sequence.signatureDate =   if (jsonObject.get("IsVideoSurvey")!!.asBoolean) "" else Utility.getCurrentDate()
        var getOfflineSignatureList: List<SeqeunceDetailSignature>? = null
        getOfflineSignatureList =  (context as BaseActivity).sequenceDetailSignatureDao.getSignatureByIdOfflineIsExist(surveyId!!, sequence.surveySequenceId!!)

        if (getOfflineSignatureList.isNotEmpty()){ (context as BaseActivity).sequenceDetailSignatureDao.deleteBySequenceId(surveyId!!,sequence.surveySequenceId!!) }

        (context as BaseActivity).sequenceDetailSignatureDao.insert(sequence)

        MessageDialog(context, if (sequence.isVideoSequence) "Survey saved successfully as video survey." else "Signature saved successfully.").show()
    }

    override fun onCleared() {
        super.onCleared()

    }
}