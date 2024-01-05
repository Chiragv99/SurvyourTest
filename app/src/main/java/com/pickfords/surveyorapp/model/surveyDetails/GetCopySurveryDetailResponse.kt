package com.pickfords.surveyorapp.model.surveyDetails

import com.google.gson.annotations.SerializedName

class GetCopySurveryDetailResponse {
    @SerializedName("Success")
    var success: Boolean = false
    @SerializedName("Message")
    var message: String = ""
    @SerializedName("Data")
    var data: Boolean = false
}