package com.pickfords.surveyorapp.model.dashboard

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "filtersurvey")
class FilterSurvey : Serializable {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "SurveyDate")
    @NonNull
    @SerializedName("SurveyDate")
    lateinit  var surveyDate: String

    @ColumnInfo(name = "showDate")
    @SerializedName("showDate")
    var showDate: String? = null


    @ColumnInfo(name = "SurveyDay")
    @SerializedName("SurveyDay")
     var surveyDay: String? = null

    @ColumnInfo(name = "isSelected")
    @SerializedName("isSelected")
    var isSelected: Boolean ? = false

}