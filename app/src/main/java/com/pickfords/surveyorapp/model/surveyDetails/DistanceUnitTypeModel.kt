package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "distance_type")
class DistanceUnitTypeModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("DistanceUnitId")
    var distanceUnitId: Int = 0

    @ColumnInfo(name = "DistanceUnit")
    @SerializedName("DistanceUnit")
    var distanceUnit: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null
}