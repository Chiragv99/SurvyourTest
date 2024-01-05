package com.pickfords.surveyorapp.model.address

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "address_list")
class AddressListModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyAddressId")
    lateinit var surveyAddressId: String

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Int = 0

    @ColumnInfo(name = "TitleName")
    @SerializedName("TitleName")
    var titleName: String? = null

    @ColumnInfo(name = "CompanyName")
    @SerializedName("CompanyName")
    var companyName: String? = null

    @ColumnInfo(name = "AddressOne")
    @SerializedName("AddressOne")
    var addressOne: String? = null

    @ColumnInfo(name = "AddressTwo")
    @SerializedName("AddressTwo")
    var addressTwo: String? = null

    @ColumnInfo(name = "CityName")
    @SerializedName("CityName")
    var cityName: String? = null

    @ColumnInfo(name = "Postcode")
    @SerializedName("Postcode")
    var postcode: String? = null

    @ColumnInfo(name = "County")
    @SerializedName("County")
    var county: String? = null

    @ColumnInfo(name = "CountryId")
    @SerializedName("CountryId")
    var countryId: String? = null

    @ColumnInfo(name = "TelePhoneNo")
    @SerializedName("TelePhoneNo")
    var telePhoneNo: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "UserId")
    @SerializedName("UserId")
    var userId: String? = null

    @ColumnInfo(name = "CountryCode")
    @SerializedName("CountryCode")
    var countryCode: String? = null

    @ColumnInfo(name = "LineNo")
    @SerializedName("LineNo")
    var lineNo: String? = null

    @ColumnInfo(name = "Salesperson_Code")
    @SerializedName("Salesperson_Code")
    var salesperson_Code: String? = null

    @ColumnInfo(name = "AddressTypeId")
    @SerializedName("AddressTypeId")
    var addressTypeId: Int? = 0

    @ColumnInfo(name = "Floor")
    @SerializedName("Floor")
    var floor: String? = null

    @ColumnInfo(name = "Access")
    @SerializedName("Access")
    var access: String? = null

    @ColumnInfo(name = "Stairs")
    @SerializedName("Stairs")
    var stairs: String? = null

    @ColumnInfo(name = "PropertyType")
    @SerializedName("PropertyTypeId")
    var propertyType: String? = null

//    @ColumnInfo(name = "Distance")
//    @SerializedName("Distance")
//    var distance: String? = null
//
//    @ColumnInfo(name = "DistanceUnitId")
//    @SerializedName("DistanceUnitId")
//    var distanceUnit: String = "0"

    @ColumnInfo(name = "Permit")
    @SerializedName("Permit")
    var permit: String = "0"

    @ColumnInfo(name = "PakringPermit")
    @SerializedName("PakringPermit")
    var parkingPermit: Boolean = false

    @ColumnInfo(name = "ParkingPermitTypeId")
    @SerializedName("ParkingPermitTypeId")
    var parkingPermitTypeId: String = ""

    @ColumnInfo(name = "Location")
    @SerializedName("Location")
    var location: String? = null

    @ColumnInfo(name = "Lift")
    @SerializedName("Lift")
    var lift: Boolean? = false

    @ColumnInfo(name = "IsLongCarry")
    @SerializedName("IsLongCarry")
    var isDestinationLongCarry: Boolean? = false

    @ColumnInfo(name = "IsShittle")
    @SerializedName("IsShittle")
    var isDestinationShittle: Boolean? = false

    @ColumnInfo(name = "IsRevisit")
    @SerializedName("IsRevisit")
    var isRevisit: Boolean? = false


    var isChangedField: Boolean? = false

    var isDelete: Boolean? = false

    fun isAddressIdInitialized() = this::surveyAddressId.isInitialized
}