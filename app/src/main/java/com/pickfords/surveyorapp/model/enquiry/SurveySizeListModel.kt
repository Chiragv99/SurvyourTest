package com.pickfords.surveyorapp.model.enquiry


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "survey_size")
class SurveySizeListModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("SurveySizeId")
    var surveySizeId: Int? = 0

    @ColumnInfo(name = "SurveySizeCode")
    @SerializedName("SurveySizeCode")
    var surveySizeCode: String? = null

    @ColumnInfo(name = "SizeDescription")
    @SerializedName("SizeDescription")
    var sizeDescription: String? = null

}