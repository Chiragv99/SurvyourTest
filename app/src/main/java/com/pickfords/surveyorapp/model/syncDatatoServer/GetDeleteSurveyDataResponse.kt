package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class GetDeleteSurveyDataResponse {

    @SerializedName("Success")
    @Expose
    private var success: Boolean? = null

    @SerializedName("Message")
    @Expose
    private var message: String? = null

    fun getSuccess(): Boolean? {
        return success
    }

    fun setSuccess(success: Boolean?) {
        this.success = success
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }
}