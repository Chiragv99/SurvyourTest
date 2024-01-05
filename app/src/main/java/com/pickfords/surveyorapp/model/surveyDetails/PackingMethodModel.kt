package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "packing_method")
class PackingMethodModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("PackingMethodId")
    var packingMethodId: Int = 0

    @ColumnInfo(name = "PackingMethod")
    @SerializedName("PackingMethod")
    var packingMethod: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

}