package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "show_legs")
class ShowLegsModel : Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id = 0


    @ColumnInfo(name = "SurveySequenceLegId")
    @SerializedName("SurveySequenceLegId")
     var surveySequenceLegId:  String? = null


    @ColumnInfo(name = "IsRevisit")
    @SerializedName("IsRevisit")
    var isRevisit: Boolean = false

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean = false

    @ColumnInfo(name = "SurveySequence")
    @SerializedName("SurveySequence")
    var surveySequence: String? = ""

    @ColumnInfo(name = "SurveySequenceLeg")
    @SerializedName("SurveySequenceLeg")
    var surveySequenceLeg: String? = null

    @ColumnInfo(name = "OriginAddress")
    @SerializedName("OriginAddress")
    var originAddress: String? = null

    @ColumnInfo(name = "OriginAddressType")
    @SerializedName("OriginAddressType")
    var originAddressType: String? = null

    @ColumnInfo(name = "OriginFloor")
    @SerializedName("OriginFloor")
    var originFloor: String? = null

    @ColumnInfo(name = "OriginVehicleAccess")
    @SerializedName("OriginVehicleAccess")
    var originVehicleAccess: String? = null

    @ColumnInfo(name = "OriginStairs")
    @SerializedName("OriginStairs")
    var originStairs: String? = null

    @ColumnInfo(name = "OriginDistance")
    @SerializedName("OriginDistance")
    var originDistance: String? = null

    @ColumnInfo(name = "OriginDistanceUnit")
    @SerializedName("OriginDistanceUnit")
    var originDistanceUnit: String? = null

    @ColumnInfo(name = "OriginDistanceUnitId")
    @SerializedName("OriginDistanceUnitId")
    var originDistanceUnitId: String? = null

    @ColumnInfo(name = "OriginPermit")
    @SerializedName("OriginPermit")
    var originPermit: String? = null

    @ColumnInfo(name = "OriginLocation")
    @SerializedName("OriginLocation")
    var originLocation: String? = null

    @ColumnInfo(name = "OriginElevator")
    @SerializedName("OriginElevator")
    var originElevator: String? = null

    @ColumnInfo(name = "DestinationAddress")
    @SerializedName("DestinationAddress")
    var destinationAddress: String? = null

    @ColumnInfo(name = "DestinationAddressType")
    @SerializedName("DestinationAddressType")
    var destinationAddressType: String? = null

    @ColumnInfo(name = "DestinationFloor")
    @SerializedName("DestinationFloor")
    var destinationFloor: String? = null

    @ColumnInfo(name = "DestinationVehicleAccess")
    @SerializedName("DestinationVehicleAccess")
    var destinationVehicleAccess: String? = null

    @ColumnInfo(name = "DestinationStairs")
    @SerializedName("DestinationStairs")
    var destinationStairs: String? = null

    @ColumnInfo(name = "DestinationDistance")
    @SerializedName("DestinationDistance")
    var destinationDistance: String? = null

    @ColumnInfo(name = "DestinationDistanceUnit")
    @SerializedName("DestinationDistanceUnit")
    var destinationDistanceUnit: String? = null

    @ColumnInfo(name = "DestinationDistanceUnitId")
    @SerializedName("DestinationDistanceUnitId")
    var destinationDistanceUnitId: String? = null

    @ColumnInfo(name = "DestinationPermit")
    @SerializedName("DestinationPermit")
    var destinationPermit: String? = null

    @ColumnInfo(name = "DestinationLocation")
    @SerializedName("DestinationLocation")
    var destinationLocation: String? = null

    @ColumnInfo(name = "DestinationElevator")
    @SerializedName("DestinationElevator")
    var destinationElevator: String? = null

    @ColumnInfo(name = "Allowance")
    @SerializedName("Allowance")
    var allowance: String? = null

    @ColumnInfo(name = "AllowanceType")
    @SerializedName("AllowanceType")
    var allowanceType: String? = null

    @ColumnInfo(name = "Distance")
    @SerializedName("Distance")
    var distance: String? = null

    @ColumnInfo(name = "DeliveryType")
    @SerializedName("DeliveryType")
    var deliveryType: String? = null

    @ColumnInfo(name = "PackingDate")
    @SerializedName("PakingDate")
    var pakingDate: String? = null

    @ColumnInfo(name = "Delivery")
    @SerializedName("Delivery")
    var delivery: String? = null

    @ColumnInfo(name = "DestinationPermitId")
    @SerializedName("DestinationPermitId")
    var destinationPermitId: String? = null

    @ColumnInfo(name = "AllowanceTypeId")
    @SerializedName("AllowanceTypeId")
    var allowanceTypeId: String? = null

    @ColumnInfo(name = "DestinationAddressId")
    @SerializedName("DestinationAddressId")
    var destinationAddressId: String? = null

    @ColumnInfo(name = "OriginAddressId")
    @SerializedName("OriginAddressId")
    var originAddressId: String? = null

    @ColumnInfo(name = "DeliveryTypeId")
    @SerializedName("DeliveryTypeId")
    var deliveryTypeId: String? = null

    @ColumnInfo(name = "DestinationVehicleAccessId")
    @SerializedName("DestinationVehicleAccessId")
    var destinationVehicleAccessId: String? = null

    @ColumnInfo(name = "OriginVehicleAccessId")
    @SerializedName("OriginVehicleAccessId")
    var originVehicleAccessId: String? = null

    @ColumnInfo(name = "DestinationAddressTypeId")
    @SerializedName("DestinationAddressTypeId")
    var destinationAddressTypeId: String? = null

    @ColumnInfo(name = "OriginAddressTypeId")
    @SerializedName("OriginAddressTypeId")
    var originAddressTypeId: String? = null

    @ColumnInfo(name = "OriginPermitId")
    @SerializedName("OriginPermitId")
    var originPermitId: String? = null

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "SurveySequenceId")
    @SerializedName("SurveySequenceId")
    var surveySequenceId: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "IsOriginLongCarry")
    @SerializedName("IsOriginLongCarry")
    var isOriginLongCarry: Boolean? = null

    @ColumnInfo(name = "IsOriginShittle")
    @SerializedName("IsOriginShittle")
    var isOriginShittle: Boolean? = null

    @ColumnInfo(name = "IsDestinationLongCarry")
    @SerializedName("IsDestinationLongCarry")
    var isDestinationLongCarry: Boolean? = null

    @ColumnInfo(name = "IsDestinationShittle")
    @SerializedName("IsDestinationShittle")
    var isDestinationShittle: Boolean? = null

    @SerializedName("isChangedField")
    var isChangedField: Boolean? = false
    var isDelete: Boolean? = false

}