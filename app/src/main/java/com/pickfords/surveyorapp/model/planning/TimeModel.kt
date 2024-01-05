package com.pickfords.surveyorapp.model.planning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "time_list")
class TimeModel {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("ActivityTimeId")
    var activityId: Int? = null

    @ColumnInfo(name = "ActivityCode")
    @SerializedName("ActivityCode")
    var activityCode: Int? = 0

    @ColumnInfo(name = "ActivityDescription")
    @SerializedName("ActivityDescription")
    var activityDescription: String? = null
}