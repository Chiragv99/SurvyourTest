package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SaveUserRoomSync {

    @SerializedName("SurveyId")
    @Expose
    private var surveyId: Int? = null

    @SerializedName("UserRoomId")
    @Expose
    private var userRoomId: Int? = null

    @SerializedName("roomId")
    @Expose
    private var roomId: Int? = null

    @SerializedName("roomName")
    @Expose
    private var roomName: String? = null

    fun getSurveyId(): Int? {
        return surveyId
    }

    fun setSurveyId(surveyId: Int?) {
        this.surveyId = surveyId
    }

    fun getUserRoomId(): Int? {
        return userRoomId
    }

    fun setUserRoomId(userRoomId: Int?) {
        this.userRoomId = userRoomId
    }

    fun getRoomId(): Int? {
        return roomId
    }

    fun setRoomId(roomId: Int?) {
        this.roomId = roomId
    }

    fun getRoomName(): String? {
        return roomName
    }

    fun setRoomName(roomName: String?) {
        this.roomName = roomName
    }
}