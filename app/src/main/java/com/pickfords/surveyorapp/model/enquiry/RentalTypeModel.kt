package com.pickfords.surveyorapp.model.enquiry


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "rental_type")
class RentalTypeModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("RentalPropertyId")
    var rentalPropertyId: Int? = 0

    @ColumnInfo(name = "RentalCode")
    @SerializedName("RentalCode")
    var rentalCode: Int? = 0

    @ColumnInfo(name = "RentalProperty")
    @SerializedName("RentalProperty")
    var rentalProperty: String? = null

}