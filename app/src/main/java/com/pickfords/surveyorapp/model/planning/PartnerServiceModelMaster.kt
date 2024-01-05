package com.pickfords.surveyorapp.model.planning

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel

class PartnerServiceModelMaster {


    @SerializedName("SurveyId")
    @Expose
    private val surveyId: Int? = null

    @SerializedName("AdditionPersonalList")
    @Expose
    private var additionPersonalList: List<AdditionalInfoPartnerModel?>? = null

    @SerializedName("PartnerList")
    @Expose
    private var partnerList: List<PartnerListModel?>? = null



    fun getAdditionPersonalList(): List<AdditionalInfoPartnerModel?>? {
        return additionPersonalList
    }


    fun getpartnerList(): List<PartnerListModel?>? {
        return partnerList
    }


    fun getSurveyId(): Int? {
        return surveyId
    }

}