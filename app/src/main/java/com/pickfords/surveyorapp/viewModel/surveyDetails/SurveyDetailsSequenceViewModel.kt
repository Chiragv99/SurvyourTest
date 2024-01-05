package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.interfaces.EditLegsInterface
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.surveyhistory.ShowLegsAdapter
import com.pickfords.surveyorapp.view.surveyhistory.SurveyDetailsSequenceFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class SurveyDetailsSequenceViewModel(var context: Context) : BaseViewModel() {

    private var isAdd: Boolean = false
    private var surveyId: String? = null
    private var onLegsEdit: EditLegsInterface? = null
    private var surveySequenceId: String? = null
    var selectedPosition: MutableLiveData<Int> = MutableLiveData()
    var selectedPositionSequence: MutableLiveData<Int> = MutableLiveData()
    var selectedPositionAddress: MutableLiveData<Int> = MutableLiveData()


    private val sequenceTypeList: MutableList<String?> = mutableListOf()
    private var sequenceTypeLiveList: MutableLiveData<List<SequenceTypeModel>> = MutableLiveData()
    var selectedSequenceTypeList: MutableLiveData<SequenceTypeModel> = MutableLiveData()
    private var sequenceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceTypeSpinnerAdapter(): ArrayAdapter<String?>? = sequenceTypeSpinnerAdapter

    private val sequenceModeList: MutableList<String?> = mutableListOf()
    private var sequenceModeLiveList: MutableLiveData<List<SequenceModeModel>> = MutableLiveData()
    private var sequenceModeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceModeSpinnerAdapter(): ArrayAdapter<String?>? = sequenceModeSpinnerAdapter



    // distance unit list code
    private var sequenceLegDestinationDistanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegDestinationDistanceTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegDestinationDistanceTypeSpinnerAdapter

    fun getSequenceLegDestinationDistanceTypeLiveList(): MutableLiveData<List<DistanceUnitTypeModel>> {
        return sequenceLegDestinationDistanceTypeLiveList
    }

    private val sequenceLegDestinationDistanceTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegDestinationDistanceTypeLiveList: MutableLiveData<List<DistanceUnitTypeModel>> =
        MutableLiveData()

    private val sequenceGroupList: MutableList<String?> = mutableListOf()
    private var sequenceGroupLiveList: MutableLiveData<List<SequenceGroupModel>> = MutableLiveData()
    private var sequenceGroupSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceGroupSpinnerAdapter(): ArrayAdapter<String?>? = sequenceGroupSpinnerAdapter

    var flagSelection = false
    private val insuranceRequiredList: MutableList<String?> = mutableListOf()
    private var insuranceRequiredSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getInsuranceRequiredSpinnerAdapter(): ArrayAdapter<String?>? =
        insuranceRequiredSpinnerAdapter

    private var insuranceRequiredLiveList: MutableLiveData<List<InsuranceRequirementModel>> =
        MutableLiveData()

    private val shippingMethodList: MutableList<String?> = mutableListOf()
    private var shippingMethodLiveList: MutableLiveData<List<ShippingMethodModel>> =
        MutableLiveData()
    private var shippingMethodSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getShoppingMethodSpinnerAdapter(): ArrayAdapter<String?>? = shippingMethodSpinnerAdapter

    private val packingMethodList: MutableList<String?> = mutableListOf()
    private var packingMethodLiveList: MutableLiveData<List<PackingMethodModel>> = MutableLiveData()
    private var packingMethodSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getPackingMethodSpinnerAdapter(): ArrayAdapter<String?>? = packingMethodSpinnerAdapter

    val sequenceList: MutableList<String?> = mutableListOf()
    internal var sequenceLiveList: MutableLiveData<List<SaveSequenceModel>> = MutableLiveData()
    private var sequenceSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceSpinnerAdapter(): ArrayAdapter<String?>? = sequenceSpinnerAdapter

    private val legList: MutableList<String?> = mutableListOf()
    private var legListSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getLegListSpinnerAdapter(): ArrayAdapter<String?>? = legListSpinnerAdapter

    private var sequenceLegList: ArrayList<ShowLegsModel> = ArrayList()
    internal var sequenceLegLiveList: MutableLiveData<List<ShowLegsModel>> = MutableLiveData()
    private var sequenceLegAdapter: ShowLegsAdapter? = null
    fun getLegList(): MutableLiveData<List<ShowLegsModel>> {
        return sequenceLegLiveList
    }

    private var selectedSequenceLiveData: MutableLiveData<SaveSequenceModel> = MutableLiveData()
    private var selectedSequenceLegLiveData: MutableLiveData<ShowLegsModel> = MutableLiveData()


    private var addressSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAddressSpinnerAdapter(): ArrayAdapter<String?>? = addressSpinnerAdapter
    private val addressList: MutableList<String?> = mutableListOf()
    private val addressListId: MutableList<String?> = mutableListOf()
    fun getAddressList(): MutableList<String?> {
        return addressList
    }

    fun getAddressListId(): MutableList<String?> {
        return addressListId
    }

    private var addressLiveList: MutableLiveData<List<AddressListModel>> = MutableLiveData()
    fun getSequenceLegOriginAddress(): MutableLiveData<List<AddressListModel>> {
        return addressLiveList
    }


    private var allowanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAllowanceTypeSpinnerAdapter(): ArrayAdapter<String?>? = allowanceTypeSpinnerAdapter
    private val allowanceTypeList: MutableList<String?> = mutableListOf()
    private var allowanceTypeLiveList: MutableLiveData<List<AllowanceTypeModel>> = MutableLiveData()

    private val deliveryTypeList: MutableList<String?> = mutableListOf()
    private var deliveryTypeLiveList: MutableLiveData<List<DeliveryTypeModel>> = MutableLiveData()
    private var deliveryTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getDeliveryTypeSpinnerAdapter(): ArrayAdapter<String?>? = deliveryTypeSpinnerAdapter


    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {


        selectedPositionAddress.value = 0

        sequenceTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceTypeList)
        sequenceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceModeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceModeList)
        sequenceModeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceGroupSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceGroupList)
        sequenceGroupSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        shippingMethodSpinnerAdapter = ArrayAdapter<String?>(
            context, android.R.layout.simple_spinner_item, shippingMethodList
        )
        shippingMethodSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        packingMethodSpinnerAdapter = ArrayAdapter<String?>(
            context, android.R.layout.simple_spinner_item, packingMethodList
        )
        packingMethodSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceList)
        sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        legListSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, legList)
        legListSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)



        insuranceRequiredSpinnerAdapter = ArrayAdapter<String?>(
            context, android.R.layout.simple_spinner_item, insuranceRequiredList
        )
        insuranceRequiredSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        sequenceTypeLiveList.observeForever {
            if (it != null) {
                sequenceTypeList.clear()
                sequenceTypeList.add("Select Sequence Type")
                for (data in it) {
                    sequenceTypeList.add(data.sequenceType)
                }
                sequenceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }
        insuranceRequiredLiveList.observeForever {
            if (it != null) {
                insuranceRequiredList.clear()
                insuranceRequiredList.add("Select Insurance Required")
                for (data in it) {
                    insuranceRequiredList.add(data.insuranceRequirement.toString())
                }
                insuranceRequiredSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceModeLiveList.observeForever {
            if (it != null) {
                sequenceModeList.clear()
                sequenceModeList.add("Select Sequence Mode")
                for (data in it) {
                    sequenceModeList.add(data.sequenceMode)
                }
                sequenceModeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceGroupLiveList.observeForever {
            if (it != null) {
                sequenceGroupList.clear()
                sequenceGroupList.add("Select Sequence Group")
                for (data in it) {
                    sequenceGroupList.add(data.sequenceGroup)
                }
                sequenceGroupSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        shippingMethodLiveList.observeForever {
            if (it != null) {
                shippingMethodList.clear()
                shippingMethodList.add("Select Shipping Method")
                for (data in it) {
                    shippingMethodList.add(data.description.toString())
                }
                shippingMethodSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        packingMethodLiveList.observeForever {
            if (it != null) {
                packingMethodList.clear()
                packingMethodList.add("Select Packing Method")
                for (data in it) {
                    packingMethodList.add(data.packingMethod)
                }
                packingMethodSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLiveList.observeForever {
            if (it != null) {

                sequenceList.clear()
                sequenceList.add("Select Sequence")

                for (data in it) {
                    val name =
                        if (data.surveySequence == null) data.surveySequence else data.surveySequence
                    sequenceList.add(name)
                }
                sequenceSpinnerAdapter?.notifyDataSetChanged()

                //  select seq. spinner item
                if (sequenceList != null && sequenceList.size > 0) {
                    if (flagSelection) {
                        selectedPosition.postValue(sequenceLiveList.value!!.size + 1)
                    } else {
                        selectedPosition.value = sequenceLiveList.value!!.size
                    }
                }
            }
        }


        sequenceLegLiveList.observeForever {
            if (it != null) {
                legList.clear()
                for (data in it) {
                    legList.add(data.surveySequenceLeg)
                }
                legListSpinnerAdapter?.notifyDataSetChanged()
            }
            if (it != null) {
                sequenceLegList.clear()
                for (data in it) {
                    sequenceLegList.add(data)
                }
                sequenceLegAdapter?.notifyDataSetChanged()
                if (sequenceLegLiveList != null && sequenceLegLiveList.value != null && sequenceLegLiveList.value!!.size > 0) {
                    selectedSequenceLegLiveData.value = sequenceLegLiveList.value!![0]
                }
            } else {
                legList.clear()
                sequenceLegList.clear()
                sequenceLegAdapter?.notifyDataSetChanged()
                legListSpinnerAdapter?.notifyDataSetChanged()
            }

        }

        addressSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, addressList)
        addressSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressLiveList.observeForever {
            if (it != null) {
                addressList.clear()
                addressListId.clear()
                for (data in it) {
                    addressList.add(data.titleName + " - " + data.cityName)
                    addressListId.add(data.surveyAddressId)
                }
                addressSpinnerAdapter?.notifyDataSetChanged()

                try {
                    if (addressList != null && addressList.size < 0 && addressList.size >= 1 && addressList.size != 2) {
                        selectedPositionAddress.value = 1
                    } else {

                        if (addressList != null && addressList.size == 2)
                            selectedPositionAddress.value = 1
                        else
                            selectedPositionAddress.value = 0
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        allowanceTypeSpinnerAdapter = ArrayAdapter<String?>(
            context, android.R.layout.simple_spinner_item, allowanceTypeList
        )
        allowanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        allowanceTypeLiveList.observeForever {
            if (it != null) {
                allowanceTypeList.clear()
                for (data in it) {
                    allowanceTypeList.add(data.name)
                }
                allowanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }


        deliveryTypeSpinnerAdapter = ArrayAdapter<String?>(
            context, android.R.layout.simple_spinner_item, deliveryTypeList
        )
        deliveryTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        deliveryTypeLiveList.observeForever {
            if (it != null) {
                deliveryTypeList.clear()
                for (data in it) {
                    deliveryTypeList.add(data.deliveryType)
                }
                deliveryTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        if ((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList() != null && context.allowanceTypeDao.getAllowanceTypeList()
                .isNotEmpty()
        ) {
            allowanceTypeLiveList.postValue(context.allowanceTypeDao.getAllowanceTypeList())

            if (context.deliveryTypeDao.getDeliveryTypeList() != null && context.deliveryTypeDao.getDeliveryTypeList()
                    .isNotEmpty()
            ) {
                deliveryTypeLiveList.postValue(context.deliveryTypeDao.getDeliveryTypeList())
            } else {
                getDeliveryTypeList(context)
            }

        } else {
            getAllowanceTypeList(context)
        }

        if (context.distanceUnitTypeDao.getDistanceUnitList() != null &&
            context.distanceUnitTypeDao.getDistanceUnitList().isNotEmpty()
        ) {
            sequenceLegDestinationDistanceTypeLiveList.postValue(context.distanceUnitTypeDao.getDistanceUnitList())
        } else {
            getDistanceTypeUnit(context)
        }
        if (context.sequenceTypeDao.getSequenceTypeList() != null && context.sequenceTypeDao.getSequenceTypeList().isNotEmpty()) {

            sequenceTypeLiveList.postValue(context.sequenceTypeDao.getSequenceTypeList())

            if (context.insuranceRequirementDao.getInsuranceRequirementList() != null && context.insuranceRequirementDao.getInsuranceRequirementList().isNotEmpty()) {
                insuranceRequiredLiveList.postValue(context.insuranceRequirementDao.getInsuranceRequirementList())


                if (context.shippingMethodDao.getShippingMethodList() != null && context.shippingMethodDao.getShippingMethodList()
                        .isNotEmpty()
                ) {
                    shippingMethodLiveList.postValue(context.shippingMethodDao.getShippingMethodList())

                    if (context.packingMethodDao.getPackingMethodList() != null && context.packingMethodDao.getPackingMethodList().isNotEmpty()) {
                        packingMethodLiveList.postValue(context.packingMethodDao.getPackingMethodList())
                    } else {
                        getPackingMethodList(context)
                    }

                } else {
                    getShippingMethodList(context)
                }


            } else {
                getInsuranceRequiredGroupList(context)
            }

        } else {
            getSequenceTypeList(context)
        }

        // Distance code
        sequenceLegDestinationDistanceTypeLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationDistanceTypeList.clear()
                for (data in it) {
                    sequenceLegDestinationDistanceTypeList.add(data.distanceUnit)
                }
                sequenceLegDestinationDistanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }
        sequenceLegDestinationDistanceTypeSpinnerAdapter = ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_item,
            sequenceLegDestinationDistanceTypeList
        )
        sequenceLegDestinationDistanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegAdapter = ShowLegsAdapter(sequenceLegList, onLegsEdit)
        sequenceLegAdapter?.notifyDataSetChanged()

    }


    fun getShowLegsAdapter(): ShowLegsAdapter? = sequenceLegAdapter

    fun getSequenceTypeLiveList(): MutableLiveData<List<SequenceTypeModel>> {
        return sequenceTypeLiveList
    }

    fun setSurveyId(id: String?, position: Int, isFirstCall: Boolean, isSavedCall: Boolean) {
        surveyId = id
        if (/*!Utility.isNetworkConnected(context) && */ (context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).isNotEmpty()) {
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false))

            if (isFirstCall) {
                if (isSavedCall) {
                    selectedPosition.value = (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).size + 1
                } else {
                    if ((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).size > 1) {
                        selectedPosition.value = 1
                        selectedPositionSequence.postValue(1)
                    } else {
                        selectedPosition.value = 0
                    }
                }
            }
        } else {
            getSequenceList(surveyId, context, isFirstCall, isSavedCall, position)
        }
    }

    fun setAdd(id: String?, position: Int, isFirstCall: Boolean, isSavedCall: Boolean) {
        surveyId = id
        if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                .isNotEmpty() && (context as BaseActivity).addressListDao.getAddressListBySurveyId(surveyId.toString()) != null && (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                surveyId.toString()
            ).isNotEmpty()
        ) {
            addressLiveList.postValue((context as BaseActivity).addressListDao.getAddressListBySurveyId(surveyId.toString()))
            addressLiveList.value?.forEach {
                Log.e("Title",it.titleName.toString())
            }
        } else {
            getAddressList(context, surveyId.toString())
        }
    }

    fun setCopySurveySequence(userId: String?, surveySequenceId: String, copyLeg: Boolean) {
        copyAllData(surveySequenceId,copyLeg)
    }

    // For Copy All Inventory Data
    private fun copyAllData(surveySequenceId: String, copyLeg: Boolean) {
        try {
            val session = Session(context)
            isAdd = true

            val model: SaveSequenceModel? = (context as BaseActivity).saveSequenceDao.getSequence(surveySequenceId)
            model?.surveySequenceId = "ADD" + (Random.nextInt().toString())
            model?.labelToUse = Utility.getNextSequenceNumber(getNextSequenceName())
            model?.surveySequence = model?.labelToUse
            model?.newRecord = true
            model?.isRevisit = false
            (context as BaseActivity).saveSequenceDao.insert(model)
            sequenceLiveList.postValue(
                (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(
                    model?.surveyId.toString(),false
                )
            )
            // Validation for copy Leg.
            if (copyLeg){
                // save legs as per new sequence
                val legs: List<ShowLegsModel> =
                    (context as BaseActivity).showLegsDao.getLegList(surveySequenceId)
                val legsTemp: ShowLegsModel = ShowLegsModel()
                isAdd = true
                for (i in legs.indices) {
                    legs[i].surveySequenceLegId = "ADD" + (Random.nextInt().toString())
                    legs[i].surveySequenceId = model?.surveySequenceId
                    legs[i].surveySequence = model?.surveySequence
                    legs[i].newRecord = true

                    legsTemp.surveyId = legs[i].surveyId
                    legsTemp.newRecord = legs[i].newRecord
                    legsTemp.surveySequenceLegId = legs[i].surveySequenceLegId
                    legsTemp.surveySequenceId = legs[i].surveySequenceId
                    legsTemp.surveySequence = legs[i].surveySequence
                    legsTemp.originAddress = legs[i].originAddress
                    legsTemp.destinationAddress = legs[i].destinationAddress
                    legsTemp.destinationAddressId = legs[i].destinationAddressId
                    legsTemp.originAddressId = legs[i].originAddressId
                    legsTemp.surveySequenceLeg = legs[i].surveySequenceLeg

                    (context as BaseActivity).showLegsDao.insertAllForCopy(legsTemp)
                }
            }


            //  select seq. spinner item
            if (sequenceList != null && sequenceList.size > 0) {
                if (flagSelection) {
                    selectedPosition.postValue(sequenceLiveList.value!!.size + 1)
                } else {
                    selectedPosition.value = sequenceLiveList.value!!.size
                }
            }

            val sequenceDetailsList: List<SequenceDetailsModel>
            if (copyLeg){
                /// Survey details with Leg
                sequenceDetailsList   = (context as BaseActivity).sequenceDetailsDao.getSurveyDetailsBySurveyIdAndSequenceId(surveyId.toString(), surveySequenceId)
                isAdd = true
            }else{
                /// Survey details without Leg
                sequenceDetailsList = (context as BaseActivity).sequenceDetailsDao.getSurveyDetailsBySurveyIdAndSequenceIdByLeg(surveyId.toString(), surveySequenceId)
                isAdd = true
            }

            if (sequenceDetailsList.isNotEmpty()){
                for (i in sequenceDetailsList.indices) {
                    sequenceDetailsList[i].surveyInventoryId = "ADD" + (Random.nextInt().toString())
                    sequenceDetailsList[i].sequenceId = model?.surveySequenceId!!
                    sequenceDetailsList[i].surveySequenceId = "0"
                    sequenceDetailsList[i].surveySequence = model?.labelToUse
                    sequenceDetailsList[i].sequence = model?.labelToUse
                    sequenceDetailsList[i].newRecord = true
                    sequenceDetailsList[i].itemImage =   sequenceDetailsList[i].itemImage
                    sequenceDetailsList[i].image =   sequenceDetailsList[i].image
                    for (j in (context as BaseActivity).showLegsDao.getLegList(model?.surveySequenceId).indices) {
                        if (sequenceDetailsList[i].surveySequenceLeg == (context as BaseActivity).showLegsDao.getLegList(
                                model?.surveySequenceId
                            )[j].surveySequenceLeg
                        ) {
                            sequenceDetailsList[i].surveySequenceLegId =
                                (context as BaseActivity).showLegsDao.getLegList(model?.surveySequenceId)[j].surveySequenceLegId
                        }
                    }
                }
                (context as BaseActivity).sequenceDetailsDao.insertAllCopy(sequenceDetailsList)
            }

            // Save Planning Details
            val planningModel = SurveyPlanningListModel()
            val planningList =
                (context as BaseActivity).surveyPlanningListDao.getPlanningListBySurveyIdList(
                    surveyId.toString(), surveySequenceId
                )
            if (planningList.isNotEmpty()) {
                for (i in planningList.indices) {
                    if (planningList[i].options!!.isNotEmpty()){
                        planningList[i].options!![0].days?.forEach {
                            it.surveyPlanningDetailId = 0
                        }
                    }

                    planningModel.surveyPlanningId =  (context as BaseActivity).surveyPlanningListDao.getPlanningList().size + 1
                    planningModel.sequenceId = model?.surveySequenceId
                    //  planningModel.surveySequence = model?.surveySequence
                    planningModel.sequenceNo = model?.surveySequence
                    planningModel.surveyId = planningList[i].surveyId
                    planningModel.options = planningList[i].options
                    planningModel.isChangedField = true
                    planningModel.createdBy = session.user?.userId?.toInt()

                }
                (context as BaseActivity).surveyPlanningListDao.insert(planningModel)

            }

            // Save Comment Details
            val commentModel = CommentsDetailModel()
            val cList =
                (context as BaseActivity).commentsDetailDao.getCommentsBySurveySequenceId(
                    surveySequenceId, surveyId.toString(),
                )
            if (cList.isNotEmpty()) {
                for (i in cList.indices) {

                    commentModel.sequenceId = model?.surveySequenceId
                    commentModel.surveyId = model?.surveyId?.toInt()
                    commentModel.ops = cList[i].ops
                    commentModel.opsConf = cList[i].opsConf
                    commentModel.customerResp = cList[i].customerResp
                    commentModel.pFReason = cList[i].pFReason
                    commentModel.general = cList[i].general
                    commentModel.sequenceNo = model?.labelToUse
                    commentModel.surveyCommentId = "ADD" + Random.nextInt()
                }
                (context as BaseActivity).commentsDetailDao.insert(commentModel)
            }

            MessageDialog(context, "Sequence copied successfully.").show()
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }

    }

    fun setSurveySequenceID(id: String?) {
        surveySequenceId = id
        if (id != null) {
            if ((context as BaseActivity).showLegsDao.getShowLegsList() != null && (context as BaseActivity).showLegsDao.getShowLegsBySurveySequenceId(
                    surveySequenceId.toString()
                ).isNotEmpty()) {
                sequenceLegLiveList.postValue(
                    (context as BaseActivity).showLegsDao.getShowLegsBySurveySequenceId(
                        surveySequenceId.toString()
                    )
                )
            } else {
                sequenceLegLiveList.postValue(null)
            }

        } else {
            getSequenceLegList(context, id)
        }
    }

    fun getSequenceModeLiveList(): MutableLiveData<List<SequenceModeModel>> {
        return sequenceModeLiveList
    }

    fun getSelectedSequenceModel(): MutableLiveData<SaveSequenceModel> {
        return selectedSequenceLiveData
    }

    fun getSelectedSequenceLegModel(): MutableLiveData<ShowLegsModel> {
        return selectedSequenceLegLiveData
    }

    fun getSequenceGroupLiveList(): MutableLiveData<List<SequenceGroupModel>> {
        return sequenceGroupLiveList
    }

    fun getInsuranceRequiredLiveList(): MutableLiveData<List<InsuranceRequirementModel>> {
        return insuranceRequiredLiveList
    }

    fun getShippingMethodLiveList(): MutableLiveData<List<ShippingMethodModel>> {
        return shippingMethodLiveList
    }

    fun getPackingMethodLiveList(): MutableLiveData<List<PackingMethodModel>> {
        return packingMethodLiveList
    }

    fun getSequenceLiveList(): MutableLiveData<List<SaveSequenceModel>> {
        return sequenceLiveList
    }

    fun getDeliveryTypeLiveList(): MutableLiveData<List<DeliveryTypeModel>> {
        return deliveryTypeLiveList
    }

    fun getAllowanceTypeLiveList(): MutableLiveData<List<AllowanceTypeModel>> {
        return allowanceTypeLiveList
    }

    //  For Delete Survey Sequence
    fun deleteSurveySequence(sequenceID: String?) {

        Utility.setOfflineTag(context, "Sequence Deleted Successfully")
        if (sequenceID!!.contains("ADD")) {
            (context as BaseActivity).saveSequenceDao.deleteBySequenceID(sequenceID)
            sequenceLiveList.postValue(
                (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId!!,false)
            )
        } else {
            val data = (context as BaseActivity).saveSequenceDao.getSequence(sequenceID)
            data?.isDelete = true
            (context as BaseActivity).saveSequenceDao.update(data)
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId!!,false))
            selectedPosition.value = sequenceLiveList.value!!.size - 1
        }
        return
    }

    //  For Get Survey Sequence
    private fun getSequenceLegList(context: Context, sequenceID: String?) {
        if (Utility.isNetworkConnected(context)) {
            isLoading.postValue(true)
            Networking(context).getServices().getSequenceLegList(sequenceID)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ShowLegsModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ShowLegsModel>>) {
                        isLoading.postValue(false)
                        sequenceLegLiveList.postValue(response.data)
                        (context as BaseActivity).showLegsDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        sequenceLegLiveList.postValue(null)
                    }
                })
        }
    }

    //  Click Listener Sequence List
    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (sequenceLiveList != null && sequenceLiveList.value != null && sequenceLiveList.value!!.size >= position && position != 0) {
                selectedSequenceLiveData.value = sequenceLiveList.value!![position - 1]
                selectedPositionSequence.postValue(position)
                setSurveySequenceID(sequenceLiveList.value!![position - 1].surveySequenceId)
                Log.e("SequenceId",sequenceLiveList.value!![position - 1].surveySequenceId.toString())
                flagSelection = false
                (context as DashboardActivity).changeSelectedSequencePosition(position)
            } else {
                selectedSequenceLiveData.value = null
                if (sequenceLegList != null && sequenceLegList.size > 0)
                    sequenceLegList.clear()
                sequenceLegAdapter?.notifyDataSetChanged()
                selectedPositionSequence.postValue(0)
            }
        }
    }

    //  Click Listener Leg List
    val clicksListenerLegs = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (sequenceLegLiveList != null && sequenceLegLiveList.value != null && sequenceLegLiveList.value!!.size > position) {
                selectedSequenceLegLiveData.value = sequenceLegLiveList.value!![position]
                for (a in 0 until addressList.size) {
                    if (addressList[a] == sequenceLegLiveList.value?.get(position)?.originAddress)
                        SurveyDetailsSequenceFragment.sequenceBinding?.spinnerFromAddressLeg?.setSelection(
                            a
                        )
                    if (addressList[a] == sequenceLegLiveList.value?.get(position)?.destinationAddress)
                        SurveyDetailsSequenceFragment.sequenceBinding?.spinnerToAddressLeg?.setSelection(a)
                }

                //   setSurveySequenceID(sequenceLiveList.value!![position - 1].surveySequenceId)
            } else {
                selectedSequenceLegLiveData.value = null
                sequenceLegList.clear()
                sequenceLegAdapter?.notifyDataSetChanged()
            }
        }
    }

    // For Get Sequence Type List
    private fun getSequenceTypeList(context: Context) {
        isLoading.postValue(true)
        Networking(context).getServices().getSequenceTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SequenceTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<SequenceTypeModel>>) {
                    sequenceTypeLiveList.postValue(response.data)

                    if ((context as BaseActivity).insuranceRequirementDao.getInsuranceRequirementList() != null && context.insuranceRequirementDao.getInsuranceRequirementList().size > 0) {
                        insuranceRequiredLiveList.postValue(context.insuranceRequirementDao.getInsuranceRequirementList())
                        isLoading.postValue(false)
                    } else {
                        getInsuranceRequiredGroupList(context)
                    }

                }

                override fun onFailed(code: Int, message: String) {
                    sequenceTypeLiveList.postValue(null)
                    if ((context as BaseActivity).insuranceRequirementDao.getInsuranceRequirementList() != null && context.insuranceRequirementDao.getInsuranceRequirementList().size > 0) {
                        insuranceRequiredLiveList.postValue(context.insuranceRequirementDao.getInsuranceRequirementList())
                    } else {
                        getInsuranceRequiredGroupList(context)
                    }
                }
            })
    }

    fun saveData(model: SaveSequenceModel?) {
        if (model?.shipmentMethodId == null) model?.shipmentMethodId = 0
        if (model?.packingMethodId == null) model?.packingMethodId = 0
        if (model?.sequenceGroupId == null) model?.sequenceGroupId = 0
        if (model?.sequenceModeId == null) model?.sequenceModeId = 0

        saveSequence(model)

    }

    private fun saveSequence(model: SaveSequenceModel?) {


        if (model?.isAddressIdInitialized() == true) {
            model.isChangedField = true
            (context as BaseActivity).saveSequenceDao.update(model)

            // Update address Ids
            if(model.surveyId!=null && model.surveyId!!.isNotEmpty()
                && model.surveySequence!=null && model.surveySequence!!.isNotEmpty()
                && model.originAddressId!=null && model.originAddressId!!.isNotEmpty()
                && model.destinationAddressId!=null && model.destinationAddressId!!.isNotEmpty()){
                (context as BaseActivity).sequenceDetailsDao.updateSequenceAddress(model.surveyId.toString(), model.surveySequence.toString(), model.originAddressId.toString(), model.destinationAddressId.toString())
            }
        } else {
            isAdd = true
            model?.surveySequenceId = "ADD" + (Random.nextInt().toString())
            if (model?.surveySequence == null) model?.surveySequence = model?.labelToUse
            model?.newRecord = true
            (context as BaseActivity).saveSequenceDao.insert(model)
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(model?.surveyId.toString(),false))

            Log.e("SaveSequence",model?.packingMethod.toString())

            // todo select seq. spinner item
            if (sequenceList != null && sequenceList.size > 0) {
                if (flagSelection) {
                    selectedPosition.postValue(sequenceLiveList.value!!.size + 1)
                } else {
                    selectedPosition.value = sequenceLiveList.value!!.size
                }
            }
        }


        Utility.setOfflineTag(context, "Sequence Saved Successfully")
    }

    // For Save Leg Data
    fun saveLegData(model: ShowLegsModel) {
        saveLeg(model)
    }

    private fun saveLeg(model: ShowLegsModel) {
        Utility.setOfflineTag(context, "Legs Saved Successfully")
        if (model.surveySequenceLegId != null) {
            model.isChangedField = true

            (context as BaseActivity).showLegsDao.update(model)
            sequenceLegLiveList.postValue(
                (context as BaseActivity).showLegsDao.getLegList(model.surveySequenceId.toString())
            )
        } else {
            isAdd = true
            model.surveySequenceLegId = "ADD" + (Random.nextInt().toString())
            model.newRecord = true
            model.surveyId = surveyId

            (context as BaseActivity).showLegsDao.insert(model)


            sequenceLegLiveList.postValue(
                (context as BaseActivity).showLegsDao.getLegList(model.surveySequenceId.toString())
            )

            // selectedPosition.value = sequenceLegLiveList.value!!.size
        }
    }

    // For Delete Sequence Leg By Id List API
    fun deleteSequenceLegById(
        surveySequenceLegId: String,
        sequenceID: String?,
        surveySequence: String
    ) {
        deleteLegs(surveySequenceLegId, sequenceID, surveySequence)
    }

    private fun deleteLegs(
        surveySequenceLegId: String,
        sequenceID: String?,
        surveySequence: String
    ) {
        Utility.setOfflineTag(context, "Leg Deleted Successfully")
        if (surveySequenceLegId.contains("ADD")) {
            (context as BaseActivity).showLegsDao.delete(surveySequenceLegId)
            sequenceLegLiveList.postValue(
                (context as BaseActivity).showLegsDao.getLegList(
                    sequenceID
                )
            )
        } else {
            val data =
                (context as BaseActivity).showLegsDao.getLeg(sequenceID, surveySequenceLegId)
            data.isDelete = true
            (context as BaseActivity).showLegsDao.update(data)

            sequenceLegLiveList.postValue(
                (context as BaseActivity).showLegsDao.getLegList(sequenceID)
            )
            //  selectedPosition.value = sequenceLegLiveList.value!!.size - 1
        }
    }

    // For Shipping Method List
    private fun getShippingMethodList(context: Context) {
        Networking(context).getServices().getShipmentMethodListForDDL().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<ShippingMethodModel>>>() {
                override fun onSuccess(response: BaseModel<List<ShippingMethodModel>>) {
                    shippingMethodLiveList.postValue(response.data)
                    if ((context as BaseActivity).packingMethodDao.getPackingMethodList() != null && context.packingMethodDao.getPackingMethodList().size > 0) {
                        packingMethodLiveList.postValue(context.packingMethodDao.getPackingMethodList())
                    } else {
                        getPackingMethodList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    shippingMethodLiveList.postValue(null)
                    if ((context as BaseActivity).packingMethodDao.getPackingMethodList() != null && context.packingMethodDao.getPackingMethodList().size > 0) {
                        packingMethodLiveList.postValue(context.packingMethodDao.getPackingMethodList())
                    } else {
                        getPackingMethodList(context)
                    }
                }
            })
    }

    private fun getInsuranceRequiredGroupList(context: Context) {
//        isLoading.postValue(true)
        Networking(context).getServices().getInsuranceRequirementList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<InsuranceRequirementModel>>>() {
                override fun onSuccess(response: BaseModel<List<InsuranceRequirementModel>>) {
                    isLoading.postValue(false)
                    insuranceRequiredLiveList.postValue(response.data)
//                    (context as BaseActivity).insuranceRequirementDao.insertAll(response.data)


                    if ((context as BaseActivity).shippingMethodDao.getShippingMethodList() != null && context.shippingMethodDao.getShippingMethodList().size > 0) {
                        shippingMethodLiveList.postValue(context.shippingMethodDao.getShippingMethodList())

                        if (context.packingMethodDao.getPackingMethodList() != null && context.packingMethodDao.getPackingMethodList().size > 0) {
                            packingMethodLiveList.postValue(context.packingMethodDao.getPackingMethodList())
                        } else {
                            getPackingMethodList(context)
                        }

                    } else {
                        getShippingMethodList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    insuranceRequiredLiveList.postValue(null)


                    if ((context as BaseActivity).shippingMethodDao.getShippingMethodList() != null && context.shippingMethodDao.getShippingMethodList().size > 0) {
                        shippingMethodLiveList.postValue(context.shippingMethodDao.getShippingMethodList())

                        if (context.packingMethodDao.getPackingMethodList() != null && context.packingMethodDao.getPackingMethodList().size > 0) {
                            packingMethodLiveList.postValue(context.packingMethodDao.getPackingMethodList())
                        } else {
                            getPackingMethodList(context)
                        }

                    } else {
                        getShippingMethodList(context)
                    }
                }
            })
    }

    // For Packing Method List API
    private fun getPackingMethodList(context: Context) {
//        isLoading.postValue(true)
        Networking(context).getServices().getPackingMethodListForDDL().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<PackingMethodModel>>>() {
                override fun onSuccess(response: BaseModel<List<PackingMethodModel>>) {
                    isLoading.postValue(false)
                    packingMethodLiveList.postValue(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    packingMethodLiveList.postValue(null)
                }
            })
    }

    private fun getDistanceTypeUnit(context: Context) {
        if (Utility.isNetworkConnected(context)) {
            Networking(context)
                .getServices()
                .getUnitTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<DistanceUnitTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<DistanceUnitTypeModel>>) {
                        sequenceLegDestinationDistanceTypeLiveList.postValue(response.data)
                        (context as BaseActivity).distanceUnitTypeDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        sequenceLegDestinationDistanceTypeLiveList.postValue(null)
                    }
                })
        }
    }

    // For Get Sequence List
    private fun getSequenceList(
        surveyId: String?,
        context: Context,
        isFirstCall: Boolean,
        isSavedCall: Boolean,
        position: Int
    ) {
        if ((context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).isNotEmpty()) {
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false))
        } else {
            sequenceLiveList.postValue(null)

        }

    }

    // For Get Sequence Detail
    private fun getSequenceDetail(surveyId: String?, surveySequenceId: String) {
        if (Utility.isNetworkConnected(context)) {
            isLoading.postValue(true)
            CoroutineScope(Dispatchers.Default).launch {
                Networking(context)
                    .getServices()
                    .getSequenceDetails(surveyId, surveySequenceId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CallbackObserver<BaseModel<List<SequenceDetailsModel>>>() {
                        override fun onSuccess(response: BaseModel<List<SequenceDetailsModel>>) {
                            isLoading.postValue(false)
                            (context as BaseActivity).sequenceDetailsDao.insertAll(response.data)
                        }

                        override fun onFailed(code: Int, message: String) {
                            isLoading.postValue(false)
                        }
                    })
            }
        }
    }


    fun setEditLegs(onLegsEdit: EditLegsInterface) {
        this.onLegsEdit = onLegsEdit
    }

    // For Customer Tab Selection
    val customerClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (sequenceTypeLiveList.value != null && sequenceTypeLiveList.value!!.isNotEmpty() && position > 0) {
                selectedSequenceTypeList.value = sequenceTypeLiveList.value!![position - 1]


            } else {
                selectedSequenceTypeList.value = null
            }

        }
    }



    // For Get Address List API
    private fun getAddressList(context: Context, surveyID: String) {
        val session = Session(context)
        isLoading.postValue(true)
        Networking(context).getServices()
            .getSurveyAddressListBySurveyId(surveyID, session.user!!.userId!!)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                    isLoading.postValue(false)
                    addressLiveList.postValue(response.data)
                    (context as BaseActivity).addressListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    addressLiveList.postValue(ArrayList())
                }
            })
    }

    // For Allowance Type List API
    private fun getAllowanceTypeList(context: Context) {

        Networking(context).getServices().getallowanceTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AllowanceTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<AllowanceTypeModel>>) {

                    allowanceTypeLiveList.postValue(response.data)
                    // (context as BaseActivity).allowanceTypeDao.insertAll(response.data)

                    if ((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList() != null && context.deliveryTypeDao.getDeliveryTypeList().size > 0) {
                        deliveryTypeLiveList.postValue(context.deliveryTypeDao.getDeliveryTypeList())
                    } else {
                        getDeliveryTypeList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {

                    allowanceTypeLiveList.postValue(null)

                    if ((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList() != null && context.deliveryTypeDao.getDeliveryTypeList().size > 0) {
                        deliveryTypeLiveList.postValue(context.deliveryTypeDao.getDeliveryTypeList())
                    } else {
                        getDeliveryTypeList(context)
                    }
                }
            })
    }

    // For Get Delivery Type List
    private fun getDeliveryTypeList(context: Context) {
        //  isLoading.postValue(true)
        Networking(context).getServices().getDeliveryTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<DeliveryTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<DeliveryTypeModel>>) {
                    isLoading.postValue(false)
                    deliveryTypeLiveList.postValue(response.data)
                    //(context as BaseActivity).deliveryTypeDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    deliveryTypeLiveList.postValue(null)
                }
            })
    }

    fun getNextSequenceName(): String {
        return if (surveyId != null) {
            (context as BaseActivity).saveSequenceDao.getLatestSequenceName(surveyId!!)
        } else {
            " "
        }
    }

}
