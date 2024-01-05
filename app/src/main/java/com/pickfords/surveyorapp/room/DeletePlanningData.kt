package com.pickfords.surveyorapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "deleteplanningdata")
class DeletePlanningData {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("SurveyPlanningId") var surveyPlanningId: Int? = 0


    @ColumnInfo(name = "isDelete")
    @SerializedName("IsDelete") var IsDelete: Boolean? = false


    @ColumnInfo(name = "surveyId")
    @SerializedName("SurveyId") var surveyId: Int? = 0
}