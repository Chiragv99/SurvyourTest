package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventory_type_selection", indices = [Index(value = ["InventoryItemId", "InventoryTypeId"], unique = true)])
class InventoryTypeSelectionModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "InventoryItemId")
    @SerializedName("InventoryItemId")
    var inventoryItemId: Int? = null,

    @ColumnInfo(name = "InventoryTypeId")
    @SerializedName("InventoryTypeId")
    var inventoryTypeId: Int? = null,

    @ColumnInfo(name = "Name")
    @SerializedName("Name")
    var name: String? = null,

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null,

    @ColumnInfo(name = "Volume")
    @SerializedName("Volume")
    var volume: Double? = null,

    @ColumnInfo(name = "Length")
    @SerializedName("Length")
    var length: Int? = null,

    @ColumnInfo(name = "Width")
    @SerializedName("Width")
    var width: Int? = null,

    @ColumnInfo(name = "Height")
    @SerializedName("Height")
    var height: Int? = null,

    @ColumnInfo(name = "MeasurementType")
    @SerializedName("CreateUnit")
    var measurementType: String? = null,

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: Int? = null,




    @ColumnInfo(name = "IsPackingType")
    @SerializedName("IsPackingType")
    var packingType: Int? = null,



    var surveyInventoryID: String? = null,
    var isSelected: Boolean = false,
    var isCurrentItem: Boolean = false,
    var isCustomize: Boolean = false,


    @ColumnInfo(name = "InventoryValue")
    @SerializedName("InventoryValue")
    var inventoryValue: Double? = null,


    @ColumnInfo(name = "InventoryNameId")
    @SerializedName("InventoryNameId")
    var inventoryNameId: String? = null,


    )