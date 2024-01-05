package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventory_floor")
class InventoryFloorModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("FloorId")
    var floorId: Int = 0

    @ColumnInfo(name = "Floor")
    @SerializedName("Floor")
    var floor: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null
}