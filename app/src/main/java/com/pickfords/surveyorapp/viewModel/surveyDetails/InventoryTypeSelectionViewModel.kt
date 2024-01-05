package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SectionHeaderSeqaunceDetails
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailViewTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.surveyDetails.InventoryTypeSelectionAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InventoryTypeSelectionViewModel(val context: Context) : BaseViewModel() {
    val inventoryTypeList: ArrayList<InventoryTypeSelectionModel> = ArrayList()
    val inventoryTypeListSearch: ArrayList<InventoryTypeSelectionModel> = ArrayList()
    private var inventoryTypeLiveList: MutableLiveData<List<InventoryTypeSelectionModel>> =
        MutableLiveData()
    var inventoryTypeLiveListSearch: MutableLiveData<List<InventoryTypeSelectionModel>> =
        MutableLiveData()
    private var inventoryTypeSelectionAdapter: InventoryTypeSelectionAdapter? = null

    fun getInventoryTypeSelectionAdapter(): InventoryTypeSelectionAdapter? =
        inventoryTypeSelectionAdapter

    fun getInventoryTypeSelectionList(): MutableLiveData<List<InventoryTypeSelectionModel>> =
        inventoryTypeLiveList

    fun getInventoryTypeSelection(): MutableLiveData<List<InventoryTypeSelectionModel>> =
        inventoryTypeLiveList

    lateinit var onInventorySelection: InventorySelection

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {
        inventoryTypeLiveList.value = null
        inventoryTypeLiveListSearch.value = null

        inventoryTypeSelectionAdapter = InventoryTypeSelectionAdapter(context, inventoryTypeList,inventoryTypeListSearch)

        inventoryTypeLiveList.observeForever {
            try {
                if (it != null) {
                    inventoryTypeList.clear()
                    // For Set Selected Item Current Item
                    if(getInventoryTypeSelectionAdapter()?.selectedPosition != -1 && getInventoryTypeSelectionAdapter()?.selectedPosition!! < it.size){
                        it[getInventoryTypeSelectionAdapter()?.selectedPosition!!].isCurrentItem = true
                    }
                    inventoryTypeList.addAll(it)
                    if (this::onInventorySelection.isInitialized) {
                        inventoryTypeSelectionAdapter?.onInventorySelection = onInventorySelection
                    }
                    inventoryTypeSelectionAdapter?.notifyItemRangeChanged(0, inventoryTypeLiveList.value!!.size)
                  //   inventoryTypeSelectionAdapter?.notifyDataSetChanged()
                }
            }catch (e: Exception){
            }
        }

        inventoryTypeLiveListSearch.observeForever {
            try {
                if (it != null) {
                    inventoryTypeListSearch.clear()
                    inventoryTypeListSearch.addAll(it)
                    if (this::onInventorySelection.isInitialized) {
                        inventoryTypeSelectionAdapter?.onInventorySelection = onInventorySelection
                    }
                    inventoryTypeSelectionAdapter?.notifyItemRangeChanged(0, inventoryTypeLiveListSearch.value!!.size)
                  //    inventoryTypeSelectionAdapter?.notifyDataSetChanged()
                }
            }catch (e:Exception){
            }
        }
        addSearchData()
    }

    // Add Search Data Here
    private fun addSearchData() {
    }

    fun  updateSelectedItemPosition(){
        getInventoryTypeSelectionAdapter()!!.selectedPosition = - 1
    }



    @SuppressLint("LongLogTag")
    fun getSurveyInventoryItemList(context: Context, itemId: String, surveyId: String, selectedFloor: String, selectedRoom: String, selectedSequence: String, selectedLeg: String) {
        Log.e("ItemId",itemId.toString())
        var inventoryList: List<InventoryTypeSelectionModel>? = null
        var inventoryListManual: List<InventoryTypeSelectionModel>? = null
        if (itemId != null && itemId == "0"){
            if ((context as BaseActivity).inventoryItemListDao.getInventoryList() != null && context.inventoryItemListDao.getInventoryList().isNotEmpty()) {
                if (surveyId.isNotBlank() && selectedFloor.isNotBlank() && selectedRoom.isNotBlank() && selectedSequence.isNotBlank()) {
                    inventoryList = context.inventoryItemListDao.getInventoryList()
                }
                inventoryTypeLiveListSearch.postValue(inventoryList)
                getInventoryTypeSelectionAdapter()!!.selectedPosition = -1;
            } else {
                callInventoryApi(itemId,surveyId,context)
            }
        }
        else{
            if ((context as BaseActivity).inventoryItemListDao.getInventoryList() != null && context.inventoryItemListDao.getInventoryListById(itemId).isNotEmpty()) {
                inventoryList = context.inventoryItemListDao.getInventoryListById(itemId)
                inventoryListManual = context.inventoryItemListDao.getInventoryListBySurveyId(itemId,surveyId)
                inventoryList = inventoryList + inventoryListManual

            } else {
                callInventoryApi(itemId,surveyId,context)
            }
        }

        var inventoryFilterList: ArrayList<InventoryTypeSelectionModel> = ArrayList()

        inventoryList?.map {
            it.isSelected = false
            it.isCurrentItem = false
            inventoryFilterList!!.add(it)
        }

        lateinit var sequenceInventoryItemList: List<SequenceDetailsModel>
        var sequenceInventoryFilterItemList: ArrayList<SequenceDetailsModel> = ArrayList()
        lateinit var customSequenceInventoryItemList: List<SequenceDetailsModel>

        if (surveyId.isNotBlank() && selectedFloor.isNotBlank() && selectedRoom.isNotBlank() && selectedSequence.isNotBlank()) {
            Log.e("QueryParams: ", "surveyId: $surveyId, selectedFloor: $selectedFloor, selectedRoom: $selectedRoom, selectedSequence: $selectedSequence, selectedLeg: $selectedLeg, selectedTypeId: $itemId")
            if (selectedLeg.isNotEmpty() && selectedLeg.lowercase() != "null") {
                sequenceInventoryItemList = context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSL(surveyId, selectedFloor, selectedRoom, selectedSequence, selectedLeg)
                sequenceInventoryFilterItemList = sequenceInventoryItemList as ArrayList<SequenceDetailsModel>;
            } else {
                sequenceInventoryItemList = context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRS(surveyId, selectedFloor, selectedRoom, selectedSequence)
                sequenceInventoryFilterItemList = sequenceInventoryItemList as ArrayList<SequenceDetailsModel>;
            }

            // For Filter List
            for (j in inventoryFilterList.indices) {
                inventoryFilterList[j].isSelected = false
            }

            if (itemId != null && itemId == "0"){
                if (sequenceInventoryFilterItemList.size > 0) {
                    for (j in inventoryFilterList.indices) {
                        for (i in sequenceInventoryFilterItemList.indices) {
                            if (inventoryFilterList[j].name == sequenceInventoryFilterItemList[i].item) {
                                Log.e("@@@", "sequenceInventoryItemList: ${sequenceInventoryFilterItemList[i].item} == inventoryList: ${inventoryFilterList[j].name} ")
                                inventoryFilterList[j].isSelected = true
                                inventoryFilterList[j].isCurrentItem = false
                                inventoryFilterList[j].surveyInventoryID = sequenceInventoryFilterItemList[i].surveyInventoryId
                                if (!sequenceInventoryFilterItemList[i].inventoryNameId.isNullOrEmpty()){
                                    inventoryFilterList[j].inventoryItemId =  Integer.parseInt(sequenceInventoryFilterItemList[i].inventoryNameId!!);
                                }

                                Log.e("Name: " + sequenceInventoryFilterItemList[i].item, "volume: " + sequenceInventoryFilterItemList[i].inventoryValue.toString())
                                Log.e("ItemNameTest",inventoryFilterList[j].name.toString())
                                inventoryFilterList[j].volume = if (sequenceInventoryFilterItemList[i].volume != null || sequenceInventoryFilterItemList[i].volume?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].volume?.toDouble() else 20.0
                                inventoryFilterList[j].inventoryValue = if (sequenceInventoryFilterItemList[i].inventoryValue != null || sequenceInventoryFilterItemList[i].inventoryValue?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].inventoryValue?.toDouble() else 20.0

                            }
                        }
                    }
                }
            }else{
                if (sequenceInventoryFilterItemList.size > 0) {
                    for (j in inventoryFilterList.indices) {
                        for (i in sequenceInventoryFilterItemList.indices) {
                            if (inventoryFilterList[j].name == sequenceInventoryFilterItemList[i].item ) {
                                Log.e("@@@", "sequenceInventoryItemList: ${sequenceInventoryFilterItemList[i].item} == inventoryList: ${inventoryFilterList[j].name} ")
                                inventoryFilterList[j].isSelected = true
                                inventoryFilterList[j].isCurrentItem = false
                                inventoryFilterList[j].surveyInventoryID = sequenceInventoryFilterItemList[i].surveyInventoryId

                                Log.e("Name: " + sequenceInventoryFilterItemList[i].item, "volume: " + sequenceInventoryFilterItemList[i].inventoryValue.toString())
                                Log.e("ItemNameTest",inventoryFilterList[j].name.toString())
                                inventoryFilterList[j].volume = if (sequenceInventoryFilterItemList[i].volume != null || sequenceInventoryFilterItemList[i].volume?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].volume?.toDouble() else 20.0

                                inventoryFilterList[j].inventoryValue = if (sequenceInventoryFilterItemList[i].inventoryValue != null || sequenceInventoryFilterItemList[i].inventoryValue?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].inventoryValue?.toDouble() else 20.0

                            }
                        }
                    }
                }
            }

            if (sequenceInventoryFilterItemList.size > 0) {
                for (j in inventoryFilterList.indices) {
                    for (i in sequenceInventoryFilterItemList.indices) {
                        if (inventoryFilterList[j].name == sequenceInventoryFilterItemList[i].item && inventoryFilterList[j].inventoryTypeId.toString() == sequenceInventoryFilterItemList[i].inventoryTypeId.toString()) {
                            Log.e(
                                "@@@",
                                "sequenceInventoryItemList: ${sequenceInventoryFilterItemList[i].item} == inventoryList: ${inventoryFilterList[j].name} "
                            )
                            inventoryFilterList[j].isSelected = true
                            inventoryFilterList[j].isCurrentItem = false
                            inventoryFilterList[j].surveyInventoryID =
                                sequenceInventoryFilterItemList[i].surveyInventoryId

                            Log.e(
                                "Name: " + sequenceInventoryFilterItemList[i].item,
                                "volume: " + sequenceInventoryFilterItemList[i].inventoryValue.toString()
                            )
                            Log.e("ItemNameTest",inventoryFilterList[j].name.toString())
                            inventoryFilterList[j].volume =
                                if (sequenceInventoryFilterItemList[i].volume != null || sequenceInventoryFilterItemList[i].volume?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].volume?.toDouble() else 20.0

                            inventoryFilterList[j].inventoryValue =
                                if (sequenceInventoryFilterItemList[i].inventoryValue != null || sequenceInventoryFilterItemList[i].inventoryValue?.isNotEmpty() == true) sequenceInventoryFilterItemList[i].inventoryValue?.toDouble() else 20.0

                        }
                    }
                }
            }

            customSequenceInventoryItemList = if (selectedLeg.isNotEmpty() && selectedLeg.lowercase() != "null") {
                context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSLCustom(surveyId, selectedFloor, selectedRoom, selectedSequence)
            } else {
                context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSCustom(surveyId, selectedFloor, selectedRoom, selectedSequence)
            }

            var customizeItems = ArrayList<InventoryTypeSelectionModel>()
            for (i in customSequenceInventoryItemList.indices) {
                if (itemId == customSequenceInventoryItemList[i].inventoryTypeId && customSequenceInventoryItemList[i].isCustomize) {

                    val customInventoryModel = InventoryTypeSelectionModel(-1,
                        null, customSequenceInventoryItemList[i].inventoryTypeId?.toInt(), customSequenceInventoryItemList[i].item,
                        0, customSequenceInventoryItemList[i].volume?.toDouble(), 0, 0, 0, "",
                        1, customSequenceInventoryItemList[i].isPackage?.let { if (it) 1 else 0 }, customSequenceInventoryItemList[i].surveyInventoryId, true, false, true, customSequenceInventoryItemList[i].inventoryValue?.toDouble(),customSequenceInventoryItemList[i].inventoryNameId)
                    // val customInventoryModel = InventoryTypeSelectionModel(-1, customSequenceInventoryItemList[i].inventoryNameId.toString() as? Int ?: 0, 1, "test", 0, 20.0, 0, 0, 0, 0, 483.toString(), true, false, true)
                    customizeItems.add(customInventoryModel)
                }
            }

            try {
                if (itemId != null && itemId == "0"){
                    inventoryFilterList = (inventoryFilterList + customizeItems) as ArrayList<InventoryTypeSelectionModel>
                    if (!inventoryFilterList.isNullOrEmpty()){
                        var reversedfilterlist: ArrayList<InventoryTypeSelectionModel> = ArrayList()
                        reversedfilterlist = inventoryFilterList.reversed() as ArrayList<InventoryTypeSelectionModel>
                        inventoryFilterList.clear()
                        for (i in reversedfilterlist.indices) {

                            if (reversedfilterlist[i].isSelected){
                                inventoryFilterList.add(reversedfilterlist[i])
                            }
                            else{
                                inventoryFilterList.remove(reversedfilterlist[i])
                            }
                        }
                    }

                    inventoryFilterList =inventoryFilterList.take(6) as ArrayList<InventoryTypeSelectionModel>
                }
                else{
                    inventoryFilterList = (inventoryFilterList + customizeItems) as ArrayList<InventoryTypeSelectionModel>
                }
            }catch (e: Exception){
            }
        }
        inventoryTypeLiveList.postValue(inventoryFilterList)
    //    getInventoryTypeSelectionAdapter()!!.selectedPosition = - 1


    }
    // Call API for Inventory
    private fun callInventoryApi(itemId: String, surveyId: String, context: BaseActivity) {
        Networking(this.context)
            .getServices()
            .getInventoryItemList(itemId,surveyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                CallbackObserver<BaseModel<List<InventoryTypeSelectionModel>>>() {
                override fun onSuccess(response: BaseModel<List<InventoryTypeSelectionModel>>) {
                    var inventoryList = context.inventoryItemListDao.getInventoryList()
                    inventoryTypeLiveList.postValue(response.data)
                    inventoryTypeLiveListSearch.postValue(inventoryList)

                }

                override fun onFailed(code: Int, message: String) {
                    inventoryTypeLiveList.postValue(null)
                }
            })
    }

    fun setInventorySelection(onInventorySelection: InventorySelection) {
        this.onInventorySelection = onInventorySelection

    }

    fun updateSavedData(
        position: Int,
        surveyInventoryId: String,
        isDelete: Boolean,
        isCustom: Boolean
    ) {

        inventoryTypeSelectionAdapter?.updateSavedData(
            position,
            surveyInventoryId,
            isDelete,
            isCustom
        )
    }

    fun removePos(
        position: Int
    ) {

        inventoryTypeSelectionAdapter?.removeItem(
            position
        )
    }

    fun addNewItemData(newInventory: InventoryTypeSelectionModel) {
        inventoryTypeSelectionAdapter?.addNewItemData(newInventory)
    }
}