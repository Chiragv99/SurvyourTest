package com.pickfords.surveyorapp.model.syncDatatoServer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPlanningAndCommentSyncResponse {
    @SerializedName("SurveyPlanningDetailList")
    @Expose
    private Boolean surveyPlanningDetailList;
    @SerializedName("SurveyCommentList")
    @Expose
    private Boolean surveyCommentList;
    @SerializedName("SurveyPictureList")
    @Expose
    private Boolean surveyPictureList;
    @SerializedName("AdditionalPersonalList")
    @Expose
    private Boolean additionalPersonalList;

    public Boolean getSurveyPlanningDetailList() {
        return surveyPlanningDetailList;
    }

    public void setSurveyPlanningDetailList(Boolean surveyPlanningDetailList) {
        this.surveyPlanningDetailList = surveyPlanningDetailList;
    }

    public Boolean getSurveyCommentList() {
        return surveyCommentList;
    }

    public void setSurveyCommentList(Boolean surveyCommentList) {
        this.surveyCommentList = surveyCommentList;
    }

    public Boolean getSurveyPictureList() {
        return surveyPictureList;
    }

    public void setSurveyPictureList(Boolean surveyPictureList) {
        this.surveyPictureList = surveyPictureList;
    }

    public Boolean getAdditionalPersonalList() {
        return additionalPersonalList;
    }

    public void setAdditionalPersonalList(Boolean additionalPersonalList) {
        this.additionalPersonalList = additionalPersonalList;
    }

}
