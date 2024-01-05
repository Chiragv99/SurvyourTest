package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "leg_access")
class LegAccessModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("LegAccessId")
    var legAccessId: Int = 0

    @ColumnInfo(name = "Name")
    @SerializedName("Name")
    var name: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

}