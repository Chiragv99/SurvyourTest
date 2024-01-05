package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.SerializedName

data class SaveInventorySync(
    @SerializedName("SurveyInventoryId") var SurveyInventoryId: String? = null,
    @SerializedName("SurveyId") var SurveyId: String? = null,
    @SerializedName("FloorId") var FloorId: String? = null,
    @SerializedName("Floor") var Floor: String? = null,
    @SerializedName("RoomId") var RoomId: String? = null,
    @SerializedName("Room") var Room: String? = null,
    @SerializedName("SequenceId") var SequenceId: String? = null,
    @SerializedName("SurveySequenceLegId") var SurveySequenceLegId: String? = null,
    @SerializedName("SurveySequence") var SurveySequence: String? = null,
    @SerializedName("SurveySequenceLeg") var SurveySequenceLeg: String? = null,
    @SerializedName("CountId") var CountId: String? = null,
    @SerializedName("Volume") var Volume: String? = null,
    @SerializedName("IsDismantle") var IsDismantle: Boolean? = null,
    @SerializedName("IsCard") var IsCard: Boolean? = null,
    @SerializedName("IsBubbleWrap") var IsBubbleWrap: Boolean? = null,
    @SerializedName("IsRemain") var IsRemain: Boolean? = null,
    @SerializedName("IsFullExportWrap") var IsFullExportWrap: Boolean? = null,
    @SerializedName("CreateLength") var CreateLength: String? = null,
    @SerializedName("CreateHeight") var CreateHeight: String? = null,
    @SerializedName("CreateWidth") var CreateWidth: String? = null,
    @SerializedName("CrateTypeId") var CrateTypeId: Int = 0,
    @SerializedName("MiscComment") var MiscComment: String? = null,
    @SerializedName("DamageComment") var DamageComment: String? = null,
    @SerializedName("ImageData") var ImageData: String? = null,
    @SerializedName("Image") var Image: String? = null,
    @SerializedName("InventoryTypeId") var InventoryTypeId: String? = null,
    @SerializedName("InventoryItemId") var InventoryItemId: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: String? = null,
    @SerializedName("InventoryItemName") var InventoryItemName: String? = null,
    @SerializedName("IsApproveAdmin") var IsApproveAdmin: Boolean? = null,
    @SerializedName("IsPackingType") var IsPackingType: Int? = null,
    @SerializedName("IsCrate") var IsCrate: Boolean? = null,
    @SerializedName("IsCustomize") var IsCustomize: Boolean? = null,
    @SerializedName("CreateUnit") var CreateUnit: Int? = null,
    @SerializedName("InventoryValue") var InventoryValue: String? = null,
    @SerializedName("IsNew") var IsNew: Boolean? = null,
    @SerializedName("isUserAdded") var isUserAdded: Boolean? = null,
    @SerializedName("IsSubItem") var isSubItem: Boolean? = null,
    @SerializedName("SubItemName") var subItemName: String? = null,
    @SerializedName("TempId") var tempId: String? = null,

)


/*
class SaveInventorySync {


    @SerializedName("InventoryItemId")
    @Expose
    private var inventoryItemId: Int? = null

    @SerializedName("InventoryTypeId")
    @Expose
    private var inventoryTypeId: Int? = null

    @SerializedName("InventoryType")
    @Expose
    private var inventoryType: String? = null

    @SerializedName("Name")
    @Expose
    private var name: String? = null

    @SerializedName("CreatedBy")
    @Expose
    private var createdBy: Int? = null

    @SerializedName("Length")
    @Expose
    private var length: Long? = null

    @SerializedName("Width")
    @Expose
    private var width: Long? = null

    @SerializedName("Height")
    @Expose
    private var height: Long? = null

    @SerializedName("Volume")
    @Expose
    private var volume: String? = null

    @SerializedName("Count")
    @Expose
    private var count: Int? = null

    @SerializedName("IsPackingType")
    @Expose
    private var isPackingType: Int? = null

    @SerializedName("NeedToApproveByAdmin")
    @Expose
    private var needToApproveByAdmin: Boolean? = null

    @SerializedName("IsApprove")
    @Expose
    private var isApprove: Boolean? = null

    @SerializedName("NavMapCode")
    @Expose
    private var navMapCode: String? = null

    @SerializedName("TotalRecords")
    @Expose
    private var totalRecords: Int? = null

    @SerializedName("IsDelete")
    @Expose
    private var isDelete: Boolean? = null

    @SerializedName("InventoryValue")
    @Expose
    private var inventoryValue: Long? = null

    @SerializedName("IsNew")
    @Expose
    private var isNew: Boolean? = null

    fun getInventoryItemId(): Int? {
        return inventoryItemId
    }

    fun setInventoryItemId(inventoryItemId: Int?) {
        this.inventoryItemId = inventoryItemId
    }

    fun getInventoryTypeId(): Int? {
        return inventoryTypeId
    }

    fun setInventoryTypeId(inventoryTypeId: Int?) {
        this.inventoryTypeId = inventoryTypeId
    }

    fun getInventoryType(): String? {
        return inventoryType
    }

    fun setInventoryType(inventoryType: String?) {
        this.inventoryType = inventoryType
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getCreatedBy(): Int? {
        return createdBy
    }

    fun setCreatedBy(createdBy: Int?) {
        this.createdBy = createdBy
    }

    fun getLength(): Long? {
        return length
    }

    fun setLength(length: Long?) {
        this.length = length
    }

    fun getWidth(): Long? {
        return width
    }

    fun setWidth(width: Long?) {
        this.width = width
    }

    fun getHeight(): Long? {
        return height
    }

    fun setHeight(height: Long?) {
        this.height = height
    }

    fun getVolume(): String? {
        return volume
    }

    fun setVolume(volume: String?) {
        this.volume = volume
    }

    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?) {
        this.count = count
    }

    fun getIsPackingType(): Int? {
        return isPackingType
    }

    fun setIsPackingType(isPackingType: Int?) {
        this.isPackingType = isPackingType
    }

    fun getNeedToApproveByAdmin(): Boolean? {
        return needToApproveByAdmin
    }

    fun setNeedToApproveByAdmin(needToApproveByAdmin: Boolean?) {
        this.needToApproveByAdmin = needToApproveByAdmin
    }

    fun getIsApprove(): Boolean? {
        return isApprove
    }

    fun setIsApprove(isApprove: Boolean?) {
        this.isApprove = isApprove
    }

    fun getNavMapCode(): String? {
        return navMapCode
    }

    fun setNavMapCode(navMapCode: String?) {
        this.navMapCode = navMapCode
    }

    fun getTotalRecords(): Int? {
        return totalRecords
    }

    fun setTotalRecords(totalRecords: Int?) {
        this.totalRecords = totalRecords
    }

    fun getIsDelete(): Boolean? {
        return isDelete
    }

    fun setIsDelete(isDelete: Boolean?) {
        this.isDelete = isDelete
    }

    fun getInventoryValue(): Long? {
        return inventoryValue
    }

    fun setInventoryValue(inventoryValue: Long?) {
        this.inventoryValue = inventoryValue
    }

    fun getIsNew(): Boolean? {
        return isNew
    }

    fun setIsNew(isNew: Boolean?) {
        this.isNew = isNew
    }

}*/
