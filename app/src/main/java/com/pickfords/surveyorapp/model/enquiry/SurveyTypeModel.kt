package com.pickfords.surveyorapp.model.enquiry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "survey_type")
class SurveyTypeModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("SurveyTypeId")
    var surveyTypeId: Int? = 0

    @ColumnInfo(name = "SurveyTypeCode")
    @SerializedName("SurveyTypeCode")
    var surveyTypeCode: Int? = 0

    @ColumnInfo(name = "SurveyType")
    @SerializedName("SurveyType")
    var surveyType: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = 0

}