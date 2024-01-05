package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "permit_type_list")
class PermitTypeListModel {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("ParkingPermitTypeId")
    var parkingPermitTypeId: Int? = null

    @ColumnInfo(name = "ParkingPermitTypeName")
    @SerializedName("ParkingPermitTypeName")
    var parkingPermitTypeName: String? = null
}