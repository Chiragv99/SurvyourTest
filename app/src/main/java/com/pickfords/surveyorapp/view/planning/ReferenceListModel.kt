package com.pickfords.surveyorapp.view.planning

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "reference_list")
class ReferenceListModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("ReferenceId")
    var referenceId: Int = 0

    @ColumnInfo(name = "ReferenceNumber")
    @SerializedName("ReferenceNumber")
    var referenceNumber: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

}