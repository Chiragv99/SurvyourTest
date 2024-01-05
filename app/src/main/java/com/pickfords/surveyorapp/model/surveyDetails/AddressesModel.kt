package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "address")
class AddressesModel {
    @ColumnInfo(name = "addressName")
    @SerializedName("addressName")
    var addressName: String? = null

    @ColumnInfo(name = "addressId")
    @SerializedName("addressId")
    var addressId: Int? = null

    @ColumnInfo(name = "titleName")
    @SerializedName("titleName")
    var titleName: String? = null

    @ColumnInfo(name = "companyName")
    @SerializedName("companyName")
    var companyName: String? = null

    @ColumnInfo(name = "address")
    @SerializedName("address")
    var address: String? = null

    @ColumnInfo(name = "addressTwo")
    @SerializedName("addressTwo")
    var addressTwo: String? = null

    @ColumnInfo(name = "postcode")
    @SerializedName("postcode")
    var postcode: String? = null

    @ColumnInfo(name = "postcodeId")
    @SerializedName("postcodeId")
    var postcodeId: String? = null

    @ColumnInfo(name = "city")
    @SerializedName("city")
    var city: String? = null

    @ColumnInfo(name = "cityId")
    @SerializedName("cityId")
    var cityId: Int? = null

    @ColumnInfo(name = "countryOne")
    @SerializedName("countryOne")
    var countryOne: String? = null

    @ColumnInfo(name = "countryOneId")
    @SerializedName("countryOneId")
    var countryOneId: String? = null

    @ColumnInfo(name = "countryTwo")
    @SerializedName("countryTwo")
    var countryTwo: String? = null

    @ColumnInfo(name = "countryTwoId")
    @SerializedName("countryTwoId")
    var countryTwoId: Int? = null

    @ColumnInfo(name = "telephoneNumber")
    @SerializedName("telephoneNumber")
    var telephoneNumber: String? = null
}