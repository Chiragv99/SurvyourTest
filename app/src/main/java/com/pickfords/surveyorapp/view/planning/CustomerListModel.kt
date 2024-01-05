package com.pickfords.surveyorapp.view.planning

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "customer_list")
class CustomerListModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("CustomerId")
    var customerId: Int = 0

    @ColumnInfo(name = "FirstName")
    @SerializedName("FirstName")
    var firstName: String? = null

    @ColumnInfo(name = "MiddleName")
    @SerializedName("MiddleName")
    var middleName: String? = null

    @ColumnInfo(name = "Surname")
    @SerializedName("Surname")
    var surname: String? = null

    @ColumnInfo(name = "MobileNo")
    @SerializedName("MobileNo")
    var mobileNo: String? = null

    @ColumnInfo(name = "EmailAddress")
    @SerializedName("EmailAddress")
    var emailAddress: String? = null

    @ColumnInfo(name = "Address")
    @SerializedName("Address")
    var address: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null



}