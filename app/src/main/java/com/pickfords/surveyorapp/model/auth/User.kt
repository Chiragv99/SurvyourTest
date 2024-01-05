package com.pickfords.surveyorapp.model.auth

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "user")
class User : Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("UserId")
    var userId: String? = null

    @ColumnInfo(name = "FirstName")
    @SerializedName("FirstName")
    var firstName: String? = null

    @ColumnInfo(name = "MiddleName")
    @SerializedName("MiddleName")
    var middleName: String? = null

    @ColumnInfo(name = "Surname")
    @SerializedName("Surname")
    var surname: String? = null

    @ColumnInfo(name = "EmailAddress")
    @SerializedName("EmailAddress")
    var emailAddress: String? = null

    @ColumnInfo(name = "Password")
    @SerializedName("Password")
    var password: String? = null

    @ColumnInfo(name = "MobileNo")
    @SerializedName("MobileNo")
    var mobileNo: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "Error")
    @SerializedName("Error")
    var error: String? = null

    @ColumnInfo(name = "JWTToken")
    @SerializedName("JWTToken")
    var jWTToken: String? = null
}