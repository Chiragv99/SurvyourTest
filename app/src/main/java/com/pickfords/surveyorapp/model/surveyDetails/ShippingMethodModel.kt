package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = "shipping_method")
class ShippingMethodModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("ShipmentMethodId")
    var shipmentMethodId: Int = 0

    @ColumnInfo(name = "ShipmentMethod")
    @SerializedName("ShipmentMethod")
    var shipmentMethod: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

    @ColumnInfo(name = "Description")
    @SerializedName ("Description")
    var description: String? = null
}