package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "survey_sequence_details")
class SurveySequenceDetailsModel : Serializable {

    @ColumnInfo(name = "SurveySequenceLegId")
    @SerializedName("SurveySequenceLegId")
    var surveySequenceLegId: Int? = null

    @ColumnInfo(name = "SurveySequenceLeg")
    @SerializedName("SurveySequenceLeg")
    var surveySequenceLeg: String? = null

    @ColumnInfo(name = "SurveySequenceId")
    @SerializedName("SurveySequenceId")
    var surveySequenceId: String? = null

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "OriginAddressId")
    @SerializedName("OriginAddressId")
    var originAddressId: String? = null

    @ColumnInfo(name = "OriginAddressTypeId")
    @SerializedName("OriginAddressTypeId")
    var originAddressTypeId: String? = null

    @ColumnInfo(name = "OriginFloor")
    @SerializedName("OriginFloor")
    var originFloor: String? = null

    @ColumnInfo(name = "OriginVehicleAccessId")
    @SerializedName("OriginVehicleAccessId")
    var originVehicleAccessId: String? = null

    @ColumnInfo(name = "OriginStairs")
    @SerializedName("OriginStairs")
    var originStairs: String? = null

    @ColumnInfo(name = "OriginDistance")
    @SerializedName("OriginDistance")
    var originDistance: String? = null

    @ColumnInfo(name = "IsOriginLongCarry")
    @SerializedName("IsOriginLongCarry")
    var isOriginLongCarry: Boolean = false

    @ColumnInfo(name = "IsOriginShittle")
    @SerializedName("IsOriginShittle")
    var isOriginShittle: Boolean? = false

    @ColumnInfo(name = "OriginPermitId")
    @SerializedName("OriginPermitId")
    var originPermitId: String? = null

    @ColumnInfo(name = "OriginLocation")
    @SerializedName("OriginLocation")
    var originLocation: String? = null

    @ColumnInfo(name = "OriginElevator")
    @SerializedName("OriginElevator")
    var originElevator: String? = null

    @ColumnInfo(name = "DestinationAddressId")
    @SerializedName("DestinationAddressId")
    var destinationAddressId: String? = null

    @ColumnInfo(name = "DestinationAddressTypeId")
    @SerializedName("DestinationAddressTypeId")
    var destinationAddressTypeId: String? = null

    @ColumnInfo(name = "DestinationFloor")
    @SerializedName("DestinationFloor")
    var destinationFloor: String? = null

    @ColumnInfo(name = "DestinationVehicleAccessId")
    @SerializedName("DestinationVehicleAccessId")
    var destinationVehicleAccessId: String? = null

    @ColumnInfo(name = "DestinationStairs")
    @SerializedName("DestinationStairs")
    var destinationStairs: String? = null

    @ColumnInfo(name = "DestinationDistance")
    @SerializedName("DestinationDistance")
    var destinationDistance: String? = null

    @ColumnInfo(name = "IsDestinationLongCarry")
    @SerializedName("IsDestinationLongCarry")
    var isDestinationLongCarry: Boolean? = false

    @ColumnInfo(name = "IsDestinationShittle")
    @SerializedName("IsDestinationShittle")
    var isDestinationShittle: Boolean? = false

    @ColumnInfo(name = "DestinationPermitId")
    @SerializedName("DestinationPermitId")
    var destinationPermitId: String? = null

    @ColumnInfo(name = "DestinationLocation")
    @SerializedName("DestinationLocation")
    var destinationLocation: String? = null

    @ColumnInfo(name = "DestinationElevator")
    @SerializedName("DestinationElevator")
    var destinationElevator: String? = null

    @ColumnInfo(name = "Allowance")
    @SerializedName("Allowance")
    var allowance: String? = null

    @ColumnInfo(name = "Distance")
    @SerializedName("Distance")
    var distance: String? = null

    @ColumnInfo(name = "DeliveryTypeId")
    @SerializedName("DeliveryTypeId")
    var deliveryTypeId: String? = null

    @ColumnInfo(name = "AllowanceTypeId")
    @SerializedName("AllowanceTypeId")
    var allowanceTypeId: String? = null

    @ColumnInfo(name = "PakingDate")
    @SerializedName("PakingDate")
    var pakingDate: String? = null

    @ColumnInfo(name = "Delivery")
    @SerializedName("Delivery")
    var delivery: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null
}