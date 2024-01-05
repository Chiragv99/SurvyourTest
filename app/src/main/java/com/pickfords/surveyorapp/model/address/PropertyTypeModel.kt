package com.pickfords.surveyorapp.model.address


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "property_type")
class PropertyTypeModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("PropertyTypeId")
    var propertyTypeId: Int? = 0

    @ColumnInfo(name = "PropertyCode")
    @SerializedName("PropertyCode")
    var propertyCode: String? = null

    @ColumnInfo(name = "PropertyType")
    @SerializedName("PropertyType")
    var propertyType: String? = null

}