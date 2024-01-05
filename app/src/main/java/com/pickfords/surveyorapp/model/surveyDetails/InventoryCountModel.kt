package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventory_count")
class InventoryCountModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("CountId")
    var countId: Int = 0

    @ColumnInfo(name = "Count")
    @SerializedName("Count")
    var count: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null


}