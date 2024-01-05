package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "submit_data")
class SubmitDataModel(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SubmitDataId")
    var submitDataId: String = "0",

    @SerializedName("SurveyId")
    var surveyId: String = "0",
)

