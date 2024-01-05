package com.pickfords.surveyorapp.view.planning

import com.google.gson.annotations.SerializedName

data class AddOptionPlanningModel(
    var list: ArrayList<AddDayPlanningModel>? = null
) {
    class AddDayPlanningModel(

        @SerializedName("AM")
        var am: String? = null,
        @SerializedName("AMDriver")
        var amDriver: String? = null,
        @SerializedName("AMPorter")
        var amPorter: String? = null,
        @SerializedName("PM")
        var pm: String,
        @SerializedName("PMDriver")
        var pmDriver: String? = null,
        @SerializedName("PMPorter")
        var pmPorter: String? = null


    )
}