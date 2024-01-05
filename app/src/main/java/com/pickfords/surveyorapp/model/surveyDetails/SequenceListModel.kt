package com.pickfords.surveyorapp.model.surveyDetails
import com.google.gson.annotations.SerializedName

class SequenceListModel {
    @SerializedName("SequenceId")
    var sequenceId: Int? = null

    @SerializedName("Sequence")
    var sequence: String? = null

    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @SerializedName("CreatedBy")
    var createdBy: Int? = null
}
