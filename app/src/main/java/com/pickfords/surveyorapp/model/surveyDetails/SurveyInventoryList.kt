package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "survey_inventory_list")
class SurveyInventoryList(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "InventoryTypeId")
    @SerializedName("InventoryTypeId")
    var inventoryTypeId: Int? = null,

    @ColumnInfo(name = "Name")
    @SerializedName("Name")
    var name: String? = null,

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null,
) {
}