package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "save_sequence")
class SaveSequenceModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveySequenceId")
    lateinit var surveySequenceId: String

    @ColumnInfo(name = "IsRevisit")
    @SerializedName("IsRevisit")
    var isRevisit: Boolean = false

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean = false

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "SurveySequence")
    @SerializedName("SurveySequence")
    var surveySequence: String? = null

    @ColumnInfo(name = "SequenceTypeId")
    @SerializedName("SequenceTypeId")
    var sequenceTypeId: Int? = null

    @ColumnInfo(name = "SequenceModeId")
    @SerializedName("SequenceModeId")
    var sequenceModeId: Int? = null

    @ColumnInfo(name = "SequenceGroupId")
    @SerializedName("SequenceGroupId")
    var sequenceGroupId: Int? = null

    @ColumnInfo(name = "InsuranceRequirementId")
    @SerializedName("InsuranceRequirementId")
    var insuranceRequirementId: Int? = null

    @ColumnInfo(name = "InsuranceAmount")
    @SerializedName("InsuranceAmount")
    var insuranceAmount: String? = null

    @ColumnInfo(name = "LabelToUse")
    @SerializedName("LabelToUse")
    var labelToUse: String? = null

    @ColumnInfo(name = "PackingMethodId")
    @SerializedName("PackingMethodId")
    var packingMethodId: Int? = null

    @ColumnInfo(name = "ShipmentMethodId")
    @SerializedName("ShipmentMethodId")
    var shipmentMethodId: Int? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "SequenceType")
    @SerializedName("SequenceType")
    var sequenceType: String? = null

    @ColumnInfo(name = "SequenceMode")
    @SerializedName("SequenceMode")
    var sequenceMode: String? = null

    @ColumnInfo(name = "ModeCode")
    @SerializedName("ModeCode")
    var modeCode: String? = null

    @ColumnInfo(name = "SequenceGroup")
    @SerializedName("SequenceGroup")
    var sequenceGroup: String? = null

    @ColumnInfo(name = "GroupCode")
    @SerializedName("GroupCode")
    var groupCode: String? = null

    @ColumnInfo(name = "InsuranceRequirement")
    @SerializedName("InsuranceRequirement")
    var insuranceRequirement: String? = null

    @ColumnInfo(name = "PackingMethod")
    @SerializedName("PackingMethod")
    var packingMethod: String? = null

    @ColumnInfo(name = "ShipmentMethod")
    @SerializedName("ShipmentMethod")
    var shipmentMethod: String? = null

    @ColumnInfo(name = "Allowance")
    @SerializedName("Allowance")
    var allowance: String? = null

    @ColumnInfo(name = "AllowanceTypeId")
    @SerializedName("AllowanceTypeId")
    var allowanceTypeId: String? = null

    @ColumnInfo(name = "Distance")
    @SerializedName("Distance")
    var distance: String? = null

    @ColumnInfo(name = "DistanceUnitId")
    @SerializedName("DistanceUnitId")
    var distanceUnitId: String = "0"

    @ColumnInfo(name = "DeliveryTypeId")
    @SerializedName("DeliveryTypeId")
    var deliveryTypeId: String? = null

    @ColumnInfo(name = "PakingDate")
    @SerializedName("PakingDate")
    var pakingDate: String? = null

    @ColumnInfo(name = "Delivery")
    @SerializedName("Delivery")
    var delivery: String? = null

    @ColumnInfo(name = "OriginAddress")
    @SerializedName("OriginAddress")
    var originAddress: String? = null

    @ColumnInfo(name = "DestinationAddress")
    @SerializedName("DestinationAddress")
    var destinationAddress: String? = null

    @ColumnInfo(name = "OriginAddressId")
    @SerializedName("OriginAddressId")
    var originAddressId: String? = null

    @ColumnInfo(name = "DestinationAddressId")
    @SerializedName("DestinationAddressId")
    var destinationAddressId: String? = null

    var isChangedField: Boolean? = false

    var isDelete: Boolean? = false

    var position: Int = -1

    fun isAddressIdInitialized() = this::surveySequenceId.isInitialized
}