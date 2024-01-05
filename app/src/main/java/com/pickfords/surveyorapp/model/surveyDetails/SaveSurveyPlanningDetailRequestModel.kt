package com.pickfords.surveyorapp.model.surveyDetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SaveSurveyPlanningDetailRequestModel(
    @SerializedName("SurveyId")
    var surveyId: String? = null,

    @SerializedName("CustomerId")
    var customerId: String? = null,

    @SerializedName("ReferenceId")
    var referenceId: String? = null,

    @SerializedName("Options")
    var options: MutableList<Option>? = null,

    @SerializedName("CreatedBy")
    var createdBy: String? = null
) {
    class Option(
        @SerializedName("OptionId")
        @Expose
        var optionId: String? = null,

        @SerializedName("Days")
        @Expose
        var days: List<Day>? = null
    ) {
        class Day {
            @SerializedName("SurveyPlanningDetailId")
            var surveyPlanningDetailId: Int? = null

            @SerializedName("AM")
            var am: String? = null

            @SerializedName("AMDriver")
            var aMDriver: Int? = null

            @SerializedName("AMPorter")
            var aMPorter: Int? = null

            @SerializedName("PM")
            var pm: String? = null

            @SerializedName("PMDriver")
            var pMDriver: Int? = null

            @SerializedName("PMPorter")
            var pMPorter: Int? = null
        }
    }
}

