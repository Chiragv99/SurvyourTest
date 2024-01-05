package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sequence_type")
class SequenceTypeModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SequenceTypeId")
    var sequenceTypeId: Int? = null

    @ColumnInfo(name = "SequenceType")
    @SerializedName("SequenceType")
    var sequenceType: String? = null

    @ColumnInfo(name = "SequenceModeId")
    @SerializedName("SequenceModeId")
    var sequenceModeId: Int? = null

    @ColumnInfo(name = "SequenceMode")
    @SerializedName("SequenceMode")
    var sequenceMode: String? = null

    @ColumnInfo(name = "SequenceGroupId")
    @SerializedName("SequenceGroupId")
    var sequenceGroupId: Int? = null

    @ColumnInfo(name = "SequenceGroup")
    @SerializedName("SequenceGroup")
    var sequenceGroup: String? = null

    @ColumnInfo(name = "PackingMethodId")
    @SerializedName("PackingMethodId")
    var packingMethodId: Int? = null

    @ColumnInfo(name = "PackingMethod")
    @SerializedName("PackingMethod")
    var packingMethod: String? = null

    @ColumnInfo(name = "ShipmentMethodId")
    @SerializedName("ShipmentMethodId")
    var shipmentMethodId: Int? = null

    @ColumnInfo(name = "ShipmentMethod")
    @SerializedName("ShipmentMethod")
    var shipmentMethod: String? = null

}