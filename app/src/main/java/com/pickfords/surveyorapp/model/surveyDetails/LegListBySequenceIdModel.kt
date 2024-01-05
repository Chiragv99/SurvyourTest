package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "leg_list")
class LegListBySequenceIdModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveySequenceLegId")
    lateinit var surveySequenceLegId: String

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean = false

    @ColumnInfo(name = "SurveySequenceLeg")
    @SerializedName("SurveySequenceLeg")
    var surveySequenceLeg: String? = null

    @ColumnInfo(name = "SurveySequenceId")
    @SerializedName("SurveySequenceId")
    var surveySequenceId: String? = null

    @ColumnInfo(name = "OriginAddressId")
    @SerializedName("OriginAddressId")
    var originAddressId: String? = null

    @ColumnInfo(name = "DestinationAddressId")
    @SerializedName("DestinationAddressId")
    var destinationAddressId: String? = null

    @ColumnInfo(name = "OriginAddress")
    @SerializedName("OriginAddress")
    var originAddress: String? = null

    @ColumnInfo(name = "DestinationAddress")
    @SerializedName("DestinationAddress")
    var destinationAddress: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    var isChangedField: Boolean? = false
    var isDelete: Boolean? = false

    @Ignore
    var position: Int = -1

    fun isSurveySequenceLegIdInitialized() = this::surveySequenceLegId.isInitialized
}