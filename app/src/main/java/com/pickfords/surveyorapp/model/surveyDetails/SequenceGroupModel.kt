package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sequence_group")
class SequenceGroupModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SequenceGroupId")
    var sequenceGroupId: Int? = null

    @ColumnInfo(name = "SequenceGroup")
    @SerializedName("SequenceGroup")
    var sequenceGroup: String? = null

}