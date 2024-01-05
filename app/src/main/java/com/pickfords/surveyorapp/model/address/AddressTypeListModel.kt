package com.pickfords.surveyorapp.model.address

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "address_type_list")
class AddressTypeListModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("AddressTypeId")
    var addressTypeId: Int = 0

    @ColumnInfo(name = "AddressCode")
    @SerializedName("AddressCode")
    var addressCode: String? = null

    @ColumnInfo(name = "Description")
    @SerializedName("Description")
    var description: String? = null

    @ColumnInfo(name = "CompanyShipTo")
    @SerializedName("CompanyShipTo")
    var companyShipTo: String? = null

    @ColumnInfo(name = "Type")
    @SerializedName("Type")
    var type: String? = null

    @ColumnInfo(name = "Name")
    @SerializedName("Name")
    var name: String? = null

    @ColumnInfo(name = "Address")
    @SerializedName("Address")
    var address: String? = null

    @ColumnInfo(name = "PostCode")
    @SerializedName("PostCode")
    var postcode: String? = null

    @ColumnInfo(name = "City")
    @SerializedName("City")
    var city: String? = null

    @ColumnInfo(name = "CountryId")
    @SerializedName("CountryId")
    var countryId: String? = null

    @ColumnInfo(name = "PhoneNo")
    @SerializedName("PhoneNo")
    var phoneNo: String? = null

}