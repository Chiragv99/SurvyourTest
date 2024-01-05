package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.surveyDetails.CommentsDetailModel
import com.pickfords.surveyorapp.model.surveyDetails.PartnerTempModel
import com.pickfords.surveyorapp.model.surveyDetails.PicturesModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel

class SyncPlanningCommentToServer {

    @SerializedName("SurveyPlanningDetailList")
    @Expose
    private var surveyPlanningDetailList: List<SurveyPlanningListModel>? = null


    @SerializedName("SurveyCommentList")
    @Expose
    private var surveyCommentList: List<CommentsDetailModel>? = null


    @SerializedName("SurveyPictureList")
    @Expose
    private var surveyPictureList: List<PicturesModel>? = null



    @SerializedName("AdditionalPersonalList")
    @Expose
    private var additionalPersonalList: List<JsonElement>? = null


    fun getSurveyPlanningDetailList(): List<SurveyPlanningListModel>? {
        return surveyPlanningDetailList
    }

    fun setSurveyPlanningDetailList(planningList: List<SurveyPlanningListModel>?) {
        this.surveyPlanningDetailList = planningList
    }


    fun getCommentList(): List<CommentsDetailModel>? {
        return surveyCommentList
    }

    fun setCommentList(commentList: List<CommentsDetailModel>?) {
        this.surveyCommentList = commentList
    }

    fun getSurveyPictureList(): List<PicturesModel>? {
        return surveyPictureList
    }

    fun setSurveyPictureList(pictureList: List<PicturesModel>?) {
        this.surveyPictureList = pictureList
    }

    fun getAdditionalPersonalList(): List<JsonElement>? {
        return additionalPersonalList
    }

    fun setAdditionalPersonalList(partnerList: List<JsonElement>?) {
        this.additionalPersonalList = partnerList
    }

}