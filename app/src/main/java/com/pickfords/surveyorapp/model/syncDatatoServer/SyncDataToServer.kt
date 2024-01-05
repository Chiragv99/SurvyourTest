package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.*


class SyncDataToServer {

    @SerializedName("SurveyList")
    @Expose
    private var surveyList: List<DashboardModel>? = null


    @SerializedName("AddressList")
    @Expose
    private var addressList: List<AddressListModel>? = null

    @SerializedName("SequenceList")
    @Expose
    private var sequenceList: List<SaveSequenceModel>? = null

    @SerializedName("SequenceLegList")
    @Expose
    private var sequenceLegList: List<ShowLegsModel>? = null


    @SerializedName("UserRoom")
    @Expose
    private var userRoom: List<SaveUserRoomSync>? = null


    @SerializedName("InventoryItemList")
    @Expose
    private var inventoryItemList: List<SaveInventorySync>? = null


    @SerializedName("SignatureList")
    @Expose
    private var signatureList: List<JsonObject>? = null



    fun getsurveyList(): List<DashboardModel>? {
        return surveyList
    }

    fun setsignatureList(surveyList: List<JsonObject>?) {
        this.signatureList = surveyList
    }

    fun getsignatureList(): List<JsonObject>? {
        return signatureList
    }

    fun setsurveyListt(surveyList: List<DashboardModel>?) {
        this.surveyList = surveyList
    }


    fun getAddressList(): List<AddressListModel>? {
        return addressList
    }

    fun setAddressList(addressList: List<AddressListModel>?) {
        this.addressList = addressList
    }

    fun getSequenceList(): List<SaveSequenceModel?>? {
        return sequenceList
    }

    fun setSequenceList(sequenceList: List<SaveSequenceModel>?) {
        this.sequenceList = sequenceList
    }

    fun getSequenceLegList(): List<ShowLegsModel>? {
        return sequenceLegList
    }

    fun setSequenceLegList(sequenceLegList: List<ShowLegsModel>?) {
        this.sequenceLegList = sequenceLegList
    }

    fun getUserRoom(): List<SaveUserRoomSync>? {
        return userRoom
    }

    fun setUserRoom(userRoom: List<SaveUserRoomSync>?) {
        this.userRoom = userRoom
    }


    fun getSignatureList(): List<JsonObject>? {
        return signatureList
    }

    fun setSignatureList(signatureList: List<JsonObject>?) {
        this.signatureList = signatureList
    }
}