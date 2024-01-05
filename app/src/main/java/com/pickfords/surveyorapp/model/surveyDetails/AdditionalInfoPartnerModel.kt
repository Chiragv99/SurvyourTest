package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "AdditionalInfoPartnerModel")
data class AdditionalInfoPartnerModel(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("AdditionalPersonalId") var Id: Int,
    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId") var surveyId: String? = "",
    @ColumnInfo(name = "FirstName")
    @SerializedName("FirstName") var firstName: String? = "",
    @ColumnInfo(name = "LastName")
    @SerializedName("LastName") var lastName: String? = " ",
    @ColumnInfo(name = "Email")
    @SerializedName("Email") var email: String? = " ",
    @ColumnInfo(name = "Phone")
    @SerializedName("Phone") var phone: String? = "0",
    @ColumnInfo(name = "Comments")
    @SerializedName("Comments") var comments: String? = " ",

    @ColumnInfo(name = "PartnerId")
    @SerializedName("PartnerId") var partnerId: String? = "",

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean? = false
)