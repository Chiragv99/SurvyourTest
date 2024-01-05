package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "comments_detail")
class CommentsDetailModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyCommentId")
    lateinit var surveyCommentId: String
//    var surveyCommentId: Int? = 0

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: Int? = null

    @ColumnInfo(name = "SequenceId")
    @SerializedName("SequenceId")
    var sequenceId: String? = null

    @ColumnInfo(name = "PFResponsiblity") //PFReason
    @SerializedName("PFResponsiblity")
    var pFReason: String? = null

    @ColumnInfo(name = "CustomerResp")
    @SerializedName("CustomerResp")
    var customerResp: String? = null

    @ColumnInfo(name = "General")
    @SerializedName("General")
    var general: String? = null

    @ColumnInfo(name = "CrewNotesAndHighValues") //Ops
    @SerializedName("CrewNotesAndHighValues")
    var ops: String? = null

    @ColumnInfo(name = "OpsConf")
    @SerializedName("OpsConf")
    var opsConf: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = 0

    @ColumnInfo(name = "SequenceNo")
    @SerializedName("SequenceNo")
    var sequenceNo: String? = ""

    var isChangedField: Boolean? = false

    fun isAddressIdInitialized() = this::surveyCommentId.isInitialized

}