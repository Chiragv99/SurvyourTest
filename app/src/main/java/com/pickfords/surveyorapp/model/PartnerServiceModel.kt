package com.pickfords.surveyorapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel

class PartnerServiceModel(
    @SerializedName("Message") val message: String? = "",
    @SerializedName("Success") val response: Boolean? = false,
    @SerializedName("Data") val data: Data? = null
)

data class Data(
    @SerializedName("AdditionPersonalList") val additionList: List<AdditionalInfoPartnerModel>,
    @SerializedName("PartnerList") val partnerList: ArrayList<PartnerListModel>
)

@Entity(tableName = "partnerListModel")
data class PartnerListModel(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("PartnerId") var Id: Int = 0,
    @ColumnInfo(name = "FirstName")
    @SerializedName("FirstName") var FirstName: String? = "",
    @ColumnInfo(name = "LastName")
    @SerializedName("LastName") var LastName: String? = "",
    @ColumnInfo(name = "Email")
    @SerializedName("Email") var Email: String? = "",
    @ColumnInfo(name = "Phone")
    @SerializedName("Phone") var Phone: String? = "",
    @ColumnInfo(name = "Comments")
    @SerializedName("Comments") var Comments: String? = "",
    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive") var IsActive: Boolean? = false,
    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy") var createdBy: String? = "",
    @ColumnInfo(name = "IsLink")
    @SerializedName("IsLink") var IsLink: Boolean? = false,
    @ColumnInfo(name = "Image")
    @SerializedName("Image") var Image: String?  = "",
    @ColumnInfo(name = "ImageData")
    @SerializedName("ImageData") var ImageData: String?  = "",
    )
