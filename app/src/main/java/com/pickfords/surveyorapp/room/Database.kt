package com.pickfords.surveyorapp.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pickfords.surveyorapp.model.DeletePartnerAndServiceContactModel
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.address.CountryListModel
import com.pickfords.surveyorapp.model.address.PropertyTypeModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.enquiry.SurveyTypeModel
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.view.planning.CustomerListModel
import com.pickfords.surveyorapp.view.planning.ReferenceListModel

@Database(
    entities = [DashboardModel::class, LanguageModel::class, AddressListModel::class, CountryListModel::class,
        SaveSequenceModel::class, SequenceTypeModel::class, SequenceGroupModel::class, SequenceModeModel::class,
        InsuranceRequirementModel::class, ShippingMethodModel::class, PackingMethodModel::class, ShowLegsModel::class,
        SequenceLegsTypeModel::class, LegAccessModel::class, LegPermitModel::class, AllowanceTypeModel::class,
        DeliveryTypeModel::class, InventoryFloorModel::class, InventoryRoomModel::class, InventoryCountModel::class,
        CustomerListModel::class, ReferenceListModel::class, LegListBySequenceIdModel::class, PartnerTempModel::class,
        SurveyPlanningListModel::class, CommentsDetailModel::class, InventoryTypeSelectionModel::class, SurveyInventoryList::class,
        PicturesModel::class, SequenceDetailsModel::class, DistanceUnitTypeModel::class, SyncDataModel::class,
        SubmitDataModel::class, AdditionalInfoPartnerModel::class, PartnerListModel::class, ActivityListPlanningModel::class,
        CodePlanningModel::class, EnquiryTypeModel::class, PermitTypeListModel::class, FilterSurvey::class,
        AddressTypeListModel::class, DeletePartnerAndServiceContactModel::class, SeqeunceDetailSignature::class,
        PropertyTypeModel::class, SurveySizeListModel::class, RentalTypeModel::class,TimeModel::class,SurveyTypeModel::class,DeletePlanningData::class,crateTypeModel::class],
    version = 43, autoMigrations = [AutoMigration(from = 42, to = 43)]
)
@TypeConverters(DataConverters::class)
abstract class Database : RoomDatabase() {

    abstract fun dashboardDao(): DashboardDao
    abstract fun languageDao(): LanguageDao
    abstract fun getSyncDao(): SyncDao
    abstract fun getSubmitDao(): SubmitDao
    abstract fun addressListDao(): AddressListDao
    abstract fun countryListDao(): CountryListDao
    abstract fun saveSequenceDao(): SaveSequenceDao
    abstract fun sequenceTypeDao(): SequenceTypeDao
    abstract fun sequenceGroupDao(): SequenceGroupDao
    abstract fun sequenceModeDao(): SequenceModeDao
    abstract fun insuranceRequirementDao(): InsuranceRequirementDao
    abstract fun shippingMethodDao(): ShippingMethodDao
    abstract fun packingMethodDao(): PackingMethodDao
    abstract fun showLegsDao(): ShowLegsDao
    abstract fun sequenceLegsTypeDao(): SequenceLegsTypeDao
    abstract fun legAccessDao(): LegAccessDao
    abstract fun legPermitDao(): LegPermitDao
    abstract fun allowanceTypeDao(): AllowanceTypeDao
    abstract fun deliveryTypeDao(): DeliveryTypeDao
    abstract fun distanceUnitTypeDao(): DistanceUnitDao
    abstract fun inventoryFloorDao(): InventoryFloorDao
    abstract fun inventoryRoomDao(): InventoryRoomDao
    abstract fun inventoryCountDao(): InventoryCountDao
    abstract fun inventoryListDao(): InventoryListDao
    abstract fun inventoryItemListDao(): InventoryItemListDao
    abstract fun customerListDao(): CustomerListDao
    abstract fun referenceListDao(): ReferenceListDao
    abstract fun surveyPlanningListDao(): SurveyPlanningListDao
    abstract fun commentsDetailsDao(): CommentsDetailDao
    abstract fun picturesDao(): PicturesDao
    abstract fun sequenceDetailsDao(): SequenceDetailsDao
    abstract fun partnerAndServiceDao(): PartnerAndServiceDao
    abstract fun partnerTempDao(): PartnerAndServiceTempDao
    abstract fun additionalInfo(): AdditionalInfoPartnerDao
    abstract fun legListDao(): LegListDao
    abstract fun activityListDao(): ActivityListDao
    abstract fun codeListDao(): CodeListDao
    abstract fun timeListDao(): TimeListDao
    abstract fun enquiryListDao(): EnquiryListDao
    abstract fun permitTypeListModelListDao(): PermitTypeListModelDao
    abstract fun addressTypeListModelDao(): AddressTypeListModelDao
    abstract fun filterSurveyDao(): FilterSurveyDao
    abstract fun deletePartnerContact(): DeletePartnerAndServiceContactDao
    abstract fun sequencedetailSignature(): SequenceDetailSignatureDao
    abstract fun rentalType(): RentalTypeDao
    abstract fun surveySize(): SurveySizeDao
    abstract fun propertyType(): PropertyTypeDao

    abstract fun surveyType(): SurveyTypeDao

    abstract fun deletePlanningData(): DeletePlanningDataDao

    abstract fun cratetTypeData(): CrateTypeDao
}