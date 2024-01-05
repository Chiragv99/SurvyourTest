package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sync_data")
class SyncDataModel(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SyncDataId")
    var syncDataId: String = "0",

    var isChangedField: Boolean? = false
)
