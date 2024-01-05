package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sequence_mode")
class SequenceModeModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SequenceModeId")
    var sequenceModeId: Int = 0

    @ColumnInfo(name = "SequenceMode")
    @SerializedName("SequenceMode")
    var sequenceMode: String? = null

}