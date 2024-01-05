package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAddressAndLegSyncResponse {
    @SerializedName("SurveyList")
    @Expose
    private var surveyList: Boolean? = null

    @SerializedName("AddressList")
    @Expose
    private var addressList: Boolean? = null

    @SerializedName("SequenceList")
    @Expose
    private var sequenceList: Boolean? = null

    @SerializedName("SequenceLegList")
    @Expose
    private var sequenceLegList: Boolean? = null

    @SerializedName("InventoryItemList")
    @Expose
    private var inventoryItemList: Boolean? = null



    @SerializedName("UserRoomList")
    @Expose
    private var userRoomList: Boolean? = null

    fun getSurveyList(): Boolean? {
        return surveyList
    }

    fun setSurveyList(surveyList: Boolean?) {
        this.surveyList = surveyList
    }

    fun getAddressList(): Boolean? {
        return addressList
    }

    fun setAddressList(addressList: Boolean?) {
        this.addressList = addressList
    }


    fun getSequenceLegList(): Boolean? {
        return sequenceLegList
    }

    fun setSequenceLegList(sequenceLegList: Boolean?) {
        this.sequenceLegList = sequenceLegList
    }

    fun getInventoryItemList(): Boolean? {
        return inventoryItemList
    }

    fun setInventoryItemList(inventoryItemList: Boolean?) {
        this.inventoryItemList = inventoryItemList
    }

    fun getUserRoomList(): Boolean? {
        return userRoomList
    }

    fun setUserRoomList(userRoomList: Boolean?) {
        this.userRoomList = userRoomList
    }

}