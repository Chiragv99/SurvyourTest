package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "code_list")
class CodePlanningModel {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("ActivityCodeId")
    var activityCodeId: Int? = null

    @ColumnInfo(name = "Code")
    @SerializedName("Code")
    var code: String? = null


    @ColumnInfo(name = "ActivityId")
    @SerializedName("ActivityId")
    var activityId: Int? = 0

    @ColumnInfo(name = "Activity")
    @SerializedName("Activity")
    var activity: String? = ""

    @ColumnInfo(name = "Discription")
    @SerializedName("Discription")
    var discription: String? = ""
}