package com.pickfords.surveyorapp.model.surveyDetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.model.planning.PartnerServiceModelMaster


class GetSurveyDetailMasterAPIModel {

    @SerializedName("SurveyList")
    @Expose
    private var surveyList: List<DashboardModel>? = null

    @SerializedName("SurveyAddressList")
    @Expose
    private var surveyAddressList: List<AddressListModel?>? = null

    @SerializedName("SurveySequenceList")
    @Expose
    private var surveySequenceList: List<SaveSequenceModel?>? = null

    @SerializedName("SurveySequenceLegList")
    @Expose
    private var surveySequenceLegList: List<ShowLegsModel?>? = null

    @SerializedName("SurveyInventoryList")
    @Expose
    private var surveyInventoryList: List<SequenceDetailsModel?>? = null

    @SerializedName("SurveyPlanningList")
    @Expose
    private var surveyPlanningList: List<SurveyPlanningListModel?>? = null

    @SerializedName("SurveyCommentList")
    @Expose
    private var surveyCommentList: List<CommentsDetailModel?>? = null

    @SerializedName("SurveyPictureList")
    @Expose
    private var surveyPictureList: List<PicturesModel?>? = null

    @SerializedName("AdditionalInfoList")
    @Expose
    private var additionalInfoList: List<PartnerServiceModelMaster?>? = null


    @SerializedName("RoomList")
    @Expose
    private var roomList: List<InventoryRoomModel?>? = null


    @SerializedName("InventoryItemList")
    @Expose
    private var inventoryItemList: List<InventoryTypeSelectionModel?>? = null


    @SerializedName("FilterSurveyDateList")
    @Expose
    private val filterSurveyDateList: List<FilterSurvey>? = null

    @SerializedName("SignatureList")
    @Expose
    private val signatureList: List<SeqeunceDetailSignature>? = null

    fun getSignatureList(): List<SeqeunceDetailSignature>? {
        return signatureList
    }

    fun getSurveyList(): List<DashboardModel?> {
        return surveyList ?: emptyList()
    }


    fun getSurveyAddressList(): List<AddressListModel?>? {
        return surveyAddressList
    }


    fun getSurveySequenceList(): List<SaveSequenceModel?>? {
        return surveySequenceList
    }


    fun getSurveySequenceLegList(): List<ShowLegsModel?>? {
        return surveySequenceLegList
    }


    fun getSurveyInventoryList(): List<SequenceDetailsModel?>? {
        return surveyInventoryList
    }


    fun getSurveyPlanningList(): List<SurveyPlanningListModel?>? {
        return surveyPlanningList
    }


    fun getRoomList(): List<InventoryRoomModel?>? {
        return roomList
    }


    fun getSurveyCommentList(): List<CommentsDetailModel?>? {
        return surveyCommentList
    }


    fun getSurveyPictureList(): List<PicturesModel?>? {
        return surveyPictureList
    }


    fun getAdditionalInfoList(): List<PartnerServiceModelMaster?>? {
        return additionalInfoList
    }


    fun getInventoryItemList(): List<InventoryTypeSelectionModel?>? {
        return inventoryItemList
    }

    fun getFilterSurveyDateList(): List<FilterSurvey?>? {
        return filterSurveyDateList
    }


}