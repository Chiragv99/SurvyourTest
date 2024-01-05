package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "activity_list")
class ActivityListPlanningModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("ActivityId")
    var activityId: Int? = null

    @ColumnInfo(name = "Activity")
    @SerializedName("Activity")
    var activity: String? = null



}