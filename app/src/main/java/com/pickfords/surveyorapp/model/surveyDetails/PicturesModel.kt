package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pictures")
class PicturesModel() {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyPicId")
    lateinit var surveyPicId: String

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "PictureByte")
    @SerializedName("PictureByte")
    var pictureByte: String? = ""


    @ColumnInfo(name = "ImageData")
    @SerializedName("ImageData")
    var imageData: String? = ""


    @ColumnInfo(name = "PictureName")
    @SerializedName("PictureName")
    var pictureName: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "IsLocalImage")
    @SerializedName("IsLocalImage")
    var isLocalImage: Boolean? = false

    var isDelete: Boolean? = false

}