package com.pickfords.surveyorapp.model.enquiry


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "enquiry_type")
class EnquiryTypeModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("EnquiryTypeId")
    var enquiryTypeId: Int? = 0

    @ColumnInfo(name = "EnquiryTypeName")
    @SerializedName("EnquiryTypeName")
    var enquiryTypeName: String? = null

}