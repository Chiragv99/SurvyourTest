package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sequence_signature")
class SeqeunceDetailSignature {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id = 0

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: Int = 0

    @ColumnInfo(name = "SurveySequenceId")
    @SerializedName("SurveySequenceId")
    var surveySequenceId: String ="0"

    @ColumnInfo(name = "Signature")
    @SerializedName("Signature")
    var signature: String? = ""

    @ColumnInfo(name = "SignatureByte")
    @SerializedName("SignatureByte")
    var signatureByte: String? = ""

    @ColumnInfo(name = "IsVideoSequence", defaultValue = "false")
    @SerializedName("IsVideoSequence")
    var isVideoSequence: Boolean = false


    @ColumnInfo(name = "IsOffline")
    @SerializedName("IsOffline")
    var isOffline: Boolean = false


    @ColumnInfo(name = "SequenceNo")
    @SerializedName("SequenceNo")
    var sequenceNo: String? = ""

    @ColumnInfo(name = "SignatureDate")
    @SerializedName("SignatureDate")
    var signatureDate : String? = ""


}