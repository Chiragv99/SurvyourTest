package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sequence_details")
class SequenceDetailsModel:SequenceDetailViewTypeModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyInventoryId")
    lateinit var surveyInventoryId: String

    @SerializedName("SurveySequenceId")
    var surveySequenceId: String ="0"

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: String? = null

    @ColumnInfo(name = "Floor")
    @SerializedName("Floor")
    var floor: String? = null

    @ColumnInfo(name = "FloorId")
    @SerializedName("FloorId")
    var floorId: String? = null

    @ColumnInfo(name = "Room")
    @SerializedName("Room")
    var room: String? = null

    @ColumnInfo(name = "RoomId")
    @SerializedName("RoomId")
    var roomId: String? = null

    @ColumnInfo(name = "Sequence")
    @SerializedName("Sequence")
    var sequence: String? = null

    @ColumnInfo(name = "SequenceId")
    @SerializedName("SequenceId")
    var sequenceId: String? = null

    @ColumnInfo(name = "SurveySequenceLeg")
    @SerializedName("SurveySequenceLeg")
    var surveySequenceLeg: String? = null

    @ColumnInfo(name = "SurveySequenceLegId")
    @SerializedName("SurveySequenceLegId")
    var surveySequenceLegId: String? = null


    /*@ColumnInfo(name = "LegFromAddressCity")
    @SerializedName("LegFromAddressCity")
    var legFromAddressCity: String? = null


    @ColumnInfo(name = "LegToAddressCity")
    @SerializedName("LegToAddressCity")
    var legToAddressCity: String? = null*/

    @ColumnInfo(name = "SequenceFromAddressCity")
    @SerializedName("SequenceFromAddressCity")
    var SequenceFromAddressCity: String? = null


    @ColumnInfo(name = "SequenceToAddressCity")
    @SerializedName("SequenceToAddressCity")
    var SequenceToAddressCity: String? = null

    /*@ColumnInfo(name = "legFromAddressId")
    var legFromAddressId: String? = null

    @ColumnInfo(name = "legToAddressId")
    var legToAddressId: String? = null*/

    @ColumnInfo(name = "SequenceFromAddressId")
    var SequenceFromAddressId: String? = null

    @ColumnInfo(name = "SequenceToAddressId")
    var SequenceToAddressId: String? = null

    @ColumnInfo(name = "InventoryItemName")
    @SerializedName("InventoryItemName")
    var inventoryItemName: String? = null

    @ColumnInfo(name = "IsCard")
    @SerializedName("IsCard")
    var isCard: Boolean? = null

    @ColumnInfo(name = "IsBubbleWrap")
    @SerializedName("IsBubbleWrap")
    var isBubbleWrap: Boolean? = null

    @ColumnInfo(name = "IsRemain")
    @SerializedName("IsRemain")
    var isRemain: Boolean? = null

    @ColumnInfo(name = "IsFullExportWrap")
    @SerializedName("IsFullExportWrap")
    var isFullExportWrap: Boolean? = null

    @ColumnInfo(name = "InventoryTypeId")
    @SerializedName("InventoryTypeId")
    var inventoryTypeId: String? = null

    @ColumnInfo(name = "InventoryNameId")
    @SerializedName("InventoryNameId")
    var inventoryNameId: String? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: String? = null

    @ColumnInfo(name = "CreateLength")
    @SerializedName("CreateLength")
    var createLength: String? = null

    @ColumnInfo(name = "CreateHeight")
    @SerializedName("CreateHeight")
    var createHeight: String? = null

    @ColumnInfo(name = "CreateWidth")
    @SerializedName("CreateWidth")
    var createWidth: String? = null

    @ColumnInfo(name = "MiscComment")
    @SerializedName("MiscComment")
    var miscComment: String? = null

    @ColumnInfo(name = "DamageComment")
    @SerializedName("DamageComment")
    var damageComment: String? = null

    @ColumnInfo(name = "IsApproveAdmin")
    @SerializedName("IsApproveAdmin")
    var isApproveAdmin: Boolean? = null

    @ColumnInfo(name = "Image")
    @SerializedName("Image")
    var image: String? = null


    @ColumnInfo(name = "Item")
    @SerializedName("Item")
    var item: String? = null

    @ColumnInfo(name = "Count")
    @SerializedName("Count")
    var count: String? = null

    @ColumnInfo(name = "Volume")
    @SerializedName("Volume")
    var volume: String? = null

    @ColumnInfo(name = "TotalVolume")
    @SerializedName("TotalVolume")
    var totalVolume: String? = null

    @ColumnInfo(name = "Comments")
    @SerializedName("Comments")
    var comments: String? = null

    @ColumnInfo(name = "Damage")
    @SerializedName("Damage")
    var damage: String? = null

    @ColumnInfo(name = "Dimenstion")
    @SerializedName("Dimenstion")
    var dimenstion: String? = null

    @ColumnInfo(name = "MeasurementType")
    @SerializedName("CreateUnit")
    var measurementType: String? = null

    @ColumnInfo(name = "IsPackage")
    @SerializedName("IsPackage")
    var isPackage: Boolean? = null

    @ColumnInfo(name = "IsDismantle")
    @SerializedName("IsDismantle")
    var isDismantle: Boolean? = null

    @ColumnInfo(name = "IsCrate")
    @SerializedName("IsCrate")
    var isCrate: Boolean = false

    @ColumnInfo(name = "IsCustomize")
    @SerializedName("IsCustomize")
    var isCustomize: Boolean = false

    @ColumnInfo(name = "ItemImage")
    @SerializedName("ItemImage")
    var itemImage: String? = null


    @ColumnInfo(name = "InventoryValue")
    @SerializedName("InventoryValue")
    var inventoryValue: String? = null



    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean? = false


    /* @ColumnInfo(name = "IsPackingType")
     @SerializedName("IsPackingType")
     var isPackingType: Int = 0*/
    @SerializedName("IsPackingType")
    var packingType: Int? = 0

    @ColumnInfo(name = "isDelete")
    var isDelete: Boolean = false


    @ColumnInfo(name = "SurveySequence")
    @SerializedName("SurveySequence")
    var surveySequence : String? = null



    var isChangedField: Boolean? = false



    @ColumnInfo(name = "IsUserAdded")
    @SerializedName("IsUserAdded")
    var isUserAdded: Boolean? = false



    @ColumnInfo(name = "CrateTypeId")
    @SerializedName("CrateTypeId")
    var crateTypeId: Int? = 0



    @ColumnInfo(name = "IsSubItem")
    @SerializedName("IsSubItem")
    var isSubItem: Boolean? = false


    @ColumnInfo(name = "SubItemName")
    @SerializedName("SubItemName")
    var subItemName: String? = ""

    /*var fromCity: String? = null
    var fromCountry: String? = null
    var toCity: String? = null
    var toCountry: String? = null*/

    var tempId: String? = ""



    fun isAddressIdInitialized() = this::surveyInventoryId.isInitialized

    override fun getViewType(): Int =1


    override fun toString(): String =
        "surveyInventoryId: $surveyInventoryId, surveySequenceId: $surveySequenceId, SurveyId: $surveyId, Item: $item"
}