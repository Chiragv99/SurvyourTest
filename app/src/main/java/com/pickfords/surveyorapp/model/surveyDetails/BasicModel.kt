package com.pickfords.surveyorapp.model.surveyDetails

import com.google.gson.annotations.SerializedName

class BasicModel(
    @SerializedName("Success")
    var success: Boolean = false,
    @SerializedName("Message")
    var message: String = ""
) {}