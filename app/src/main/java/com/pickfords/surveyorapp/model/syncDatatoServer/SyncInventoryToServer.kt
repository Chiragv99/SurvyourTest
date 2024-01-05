package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.SerializedName

class SyncInventoryToServer {

    @SerializedName("SurveyInventoryList")
    var surveyInventoryList: List<SaveInventorySync>? = null
}