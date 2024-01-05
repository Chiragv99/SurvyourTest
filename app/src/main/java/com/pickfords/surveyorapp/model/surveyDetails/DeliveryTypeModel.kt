package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "delivery_type")
class DeliveryTypeModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("DeliveryTypeId")
    var deliveryTypeId: Int = 0

    @ColumnInfo(name = "DeliveryType")
    @SerializedName("DeliveryType")
    var deliveryType: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

}