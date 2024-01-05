package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "insurance_requirement")
class InsuranceRequirementModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("InsuranceRequirementId")
    var insuranceRequirementId: Int = 0

    @ColumnInfo(name = "InsuranceRequirement")
    @SerializedName("InsuranceRequirement")
    var insuranceRequirement: String? = null

}