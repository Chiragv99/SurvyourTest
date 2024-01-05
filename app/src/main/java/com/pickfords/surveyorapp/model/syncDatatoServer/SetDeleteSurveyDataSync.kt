package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SetDeleteSurveyDataSync {

    @SerializedName("RowsId")
    @Expose
    private var rowsId: Int? = null

    @SerializedName("Types")
    @Expose
    private var types: String? = null

    @SerializedName("PkId")
    @Expose
    private var pkId: Int? = null

    @SerializedName("SurveyId")
    @Expose
    private var surveyId: Int? = null

    fun getRowsId(): Int? {
        return rowsId
    }

    fun setRowsId(rowsId: Int?) {
        this.rowsId = rowsId
    }

    fun getTypes(): String? {
        return types
    }

    fun setTypes(types: String?) {
        this.types = types
    }

    fun getPkId(): Int? {
        return pkId
    }

    fun setPkId(pkId: Int?) {
        this.pkId = pkId
    }

    fun getSurveyId(): Int? {
        return surveyId
    }

    fun setSurveyId(surveyId: Int?) {
        this.surveyId = surveyId
    }
}