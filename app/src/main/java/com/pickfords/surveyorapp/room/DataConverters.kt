package com.pickfords.surveyorapp.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel


class DataConverters {
    @TypeConverter
    fun fromOptionList(value: List<SurveyPlanningListModel.Option>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SurveyPlanningListModel.Option>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOptionList(value: String): List<SurveyPlanningListModel.Option> {
        val gson = Gson()
        val type = object : TypeToken<List<SurveyPlanningListModel.Option>>() {}.type
        return gson.fromJson(value, type)
    }
}