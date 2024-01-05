package com.pickfords.surveyorapp.model


import com.google.gson.annotations.SerializedName
import com.pickfords.surveyorapp.model.address.*
import com.pickfords.surveyorapp.model.enquiry.*
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.surveyDetails.*

class MasterTableModel(
    @SerializedName("Message") val message: String? = "",
    @SerializedName("Success") val response: Boolean? = false,
    @SerializedName("Data") val data: MasterData? = null
)

data class MasterData(
    @SerializedName("SurveyActivityList") val surveyActivityList: List<ActivityListPlanningModel>,
    @SerializedName("ActivityCodeList") val activityCodeList: List<CodePlanningModel>,
    @SerializedName("ActivityTimeList") val timeList: List<TimeModel>,
    @SerializedName("LanguageList") val languageList: List<LanguageModel>,
    @SerializedName("EnquiryTypeList") val enquiryTypeList: List<EnquiryTypeModel>,
    @SerializedName("CountryList") val countryList: List<CountryListModel>,
    @SerializedName("SurveySizeList") val surveySizeList: List<SurveySizeListModel>,
    @SerializedName("RentalPropertyList") val rentalPropertyList: List<RentalTypeModel>,
    @SerializedName("AddressTypeList") val addressTypeList: List<AddressTypeListModel>,
    @SerializedName("PropertyTypeList") val propertyTypeList: List<PropertyTypeModel>,
    @SerializedName("SequenceTypeList") val sequenceTypeList: List<SequenceTypeModel>,
    @SerializedName("PackingMethodList") val packingMethodList: List<PackingMethodModel>,
    @SerializedName("ShipmentMethodList") val shipmentMethodList: List<ShippingMethodModel>,
    @SerializedName("AllowanceTypeList") val allowanceTypeList: List<AllowanceTypeModel>,
    @SerializedName("DistanceUnitList") val distanceUnitList: List<DistanceUnitTypeModel>,
    @SerializedName("FloorList") val floorList: List<InventoryFloorModel>,
    @SerializedName("InventoryTypeList") val inventoryTypeList: List<SurveyInventoryList>,
    @SerializedName("SurveyTypeList") val surveyTypeList: List<SurveyTypeModel>,
    @SerializedName("InventoryItemList") val inventoryItemList: List<InventoryTypeSelectionModel>,
    @SerializedName("CrateTypeList") val crateTypeList: List<crateTypeModel>,
    @SerializedName("PermitList") val permitList: List<LegPermitModel>,
)


