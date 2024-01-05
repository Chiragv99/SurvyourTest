package com.pickfords.surveyorapp.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pickfords.surveyorapp.model.MasterTableModel
import com.pickfords.surveyorapp.model.PartnerServiceModel
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.address.CountryListModel
import com.pickfords.surveyorapp.model.address.PropertyTypeModel
import com.pickfords.surveyorapp.model.auth.User
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.model.syncDatatoServer.*
import com.pickfords.surveyorapp.model.syncDatatoServer.GetAddressAndLegSyncResponse
import com.pickfords.surveyorapp.model.syncDatatoServer.GetPlanningAndCommentSyncResponse
import com.pickfords.surveyorapp.model.syncDatatoServer.SyncDataToServer
import com.pickfords.surveyorapp.model.syncDatatoServer.SyncInventoryToServer
import com.pickfords.surveyorapp.model.syncDatatoServer.SyncPlanningCommentToServer
import com.pickfords.surveyorapp.view.planning.CustomerListModel
import com.pickfords.surveyorapp.view.planning.ReferenceListModel
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface APIInterface {
    @POST("Login/LoginUser")
    fun login(@Body requestBody: RequestBody): Observable<BaseModel<User>>

    @GET("Login/ForgotPassword/{Path}")
    fun forgotPassword(@Path("Path") path: String): Observable<BaseModel<User>>

    // For Get All Master Data
     @GET("SurveyGetData/GetAllMasterData")
    fun getMasterTableApi(): Observable<MasterTableModel>


    // Base Url Changed new
    // For Get All Survey Detail Data
    @GET("SurveyGetData/GetSurveyDataList/{UserId}")
    fun getSurveyDetailMasterApi(@Path("UserId") userId: String): Observable<BaseModel<GetSurveyDetailMasterAPIModel>>

    @POST("Survey/GetSurveyDetailList")
    fun getSurveyDetailList(@Body jsonObject: JsonObject): Observable<BaseModel<List<DashboardModel>>>

    @POST("Survey/SaveSurveyDetail")
    fun saveSurveyDetail(@Body body: DashboardModel?): Observable<BaseModel<List<User>>>

    @GET("Survey/GetSurveyPictureList/{SurveyId}")
    fun getSurveyPictureList(
        @Path("SurveyId") surveyId: String
    ): Observable<BaseModel<List<PicturesModel>>>

    @Multipart
    @POST("Survey/SaveSurveyPicture")
    fun saveSurveyPictureBase(
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>?
    ): Observable<BaseModel<List<PicturesModel>>>

    @GET("Language/GetLanguageListForDDL")
    fun getLanguageList(): Observable<BaseModel<List<LanguageModel>>>


    @GET("Customer/GetCustomerListForDDL")
    fun getCustomerList(): Observable<BaseModel<List<CustomerListModel>>>

    @GET("Customer/GetReferenceListForDDL")
    fun getReferenceList(): Observable<BaseModel<List<ReferenceListModel>>>

    @GET("Country/GetCountryListForDDL")
    fun getCountryList(): Observable<BaseModel<List<CountryListModel>>>

    // Api used ofr Partner & service module
    // ................................................................................

    @GET("/api/AdditionalPersonal/GetPartnerServiceListByServeyId/{SurveyId}")
    fun getPartnerServiceList(@Path("SurveyId") surveyId: String): Observable<PartnerServiceModel>

    @DELETE("/api/AdditionalPersonal/DeleteAdditionalPersonal/{Id}")
    fun removePartnerContact(@Path("Id") id: Int?): Observable<BaseModel<Boolean>>

    @POST("/api/AdditionalPersonal/SaveAdditionalPersonal")
    fun savePartnerServiceList(@Body jsonObject: JsonObject): Observable<BaseModel<Boolean>>

    //.............................................................................
    @DELETE("Survey/DeleteSurveyAddress/{SurveyAddressId}/{UpdatedBy}")
    fun deleteSurveyAddress(
        @Path("SurveyAddressId") SurveyAddressId: String, @Path("UpdatedBy") updatedBy: String
    ): Observable<BaseModel<List<AddressListModel>>>

    @GET("Survey/GetSurveyAddressListBySurveyId/{SurveyId}/{UserId}/")
    fun getSurveyAddressListBySurveyId(
        @Path("SurveyId") surveyId: String,
        @Path("UserId") userId: String
    ): Observable<BaseModel<List<AddressListModel>>>

    @POST("Survey/SaveSurveyAddress")
    fun saveSurveyAddress(
        @Body body: AddressListModel?
    ): Observable<BaseModel<List<AddressListModel>>>

    @GET("/api/Survey/GetParkingPermitType")
    fun getPermitType(): Observable<BaseModel<List<PermitTypeListModel>>>

    @GET("SurveySequences/GetSequenceTypeListForDDL")
    fun getSequenceTypeList(): Observable<BaseModel<List<SequenceTypeModel>>>

    @GET("SurveySequences/GetSequenceModeListForDDL")
    fun getSequenceModeList(): Observable<BaseModel<List<SequenceModeModel>>>

    @GET("SurveySequences/GetSequenceGroupListForDDL")
    fun getSequenceGroupListForDDL(): Observable<BaseModel<List<SequenceGroupModel>>>

    @GET("SurveySequences/GetInsuranceRequirementListForDDL")
    fun getInsuranceRequirementList(): Observable<BaseModel<List<InsuranceRequirementModel>>>

    @GET("SurveySequences/GetSequenceListForDDL")
    fun getSequenceListForDDL(): Observable<BaseModel<List<SequenceListModel>>>

    @GET("SurveySequences/GetPackingMethodForDDL")
    fun getPackingMethodListForDDL(): Observable<BaseModel<List<PackingMethodModel>>>

    @GET("SurveySequences/GetShipmentMethodForDDL")
    fun getShipmentMethodListForDDL(): Observable<BaseModel<List<ShippingMethodModel>>>

    @GET("SurveySequences/GetLegTypeForDDL")
    fun getSequenceLegtypeListForDDL(): Observable<BaseModel<List<SequenceLegsTypeModel>>>

    @GET("SurveySequences/GetLegAccessForDDL")
    fun getSequenceLegAccessList(): Observable<BaseModel<List<LegAccessModel>>>

    @GET("SurveySequences/GetPermitForDDL")
    fun getSequenceLegPermitList(): Observable<BaseModel<List<LegPermitModel>>>

    @GET("SurveySequences/GetAllowanceTypeForDDL")
    fun getallowanceTypeList(): Observable<BaseModel<List<AllowanceTypeModel>>>

    @GET("SurveySequences/GetDistanceUnitListForDDL")
    fun getUnitTypeList(): Observable<BaseModel<List<DistanceUnitTypeModel>>>

    @GET("SurveySequences/GetDeliveryTypeForDDL")
    fun getDeliveryTypeList(): Observable<BaseModel<List<DeliveryTypeModel>>>

    @GET("SurveySequences/GetSequenceListBySurveyId/{SurveyId}")
    fun getSequenceList(@Path("SurveyId") surveyId: String?): Observable<BaseModel<List<SaveSequenceModel>>>

    @DELETE("SurveyInventory/DeleteSequenceInventoryItem/{SequenceInventoryItemId}")
    fun deleteSequenceInventoryItem(
        @Path("SequenceInventoryItemId") sequenceInventoryItemId: String?
    ): Observable<BaseModel<List<SaveSequenceModel>>>

    /**
    for legs
     * */
    @GET("SurveySequences/GetLegListShowById/{SurveySequenceId}")
    fun getSequenceLegList(
        @Path("SurveySequenceId") SurveySequenceId: String?
    ): Observable<BaseModel<List<ShowLegsModel>>>

    @GET("SurveySequences/CopySurveySequance/{SurveySequenceId}/{LoggedInUserId}")
    fun getCopySurveySequence(
        @Path("SurveySequenceId") SurveySequenceId: String?,
        @Path("LoggedInUserId") UpdateBy: String?
    ): Observable<GetCopySurveryDetailResponse>

    @GET("SurveyComment/GetSurveyCommentById/{SurveyId}/{SequenceId}")
    fun getSurveyCommentBySurveyId(
        @Path("SurveyId") surveyId: String,
        @Path("SequenceId") sequenceId: String
    ): Observable<BaseModel<CommentsDetailModel>>

    @POST("SurveyComment/SaveSurveyComment")
    fun saveSurveyComment(
        @Body body: CommentsDetailModel?
    ): Observable<BaseModel<List<CommentsDetailModel>>>

    @POST("SurveySequences/SaveSequanceLeg")
    fun saveSequenceLeg(
        @Body body: ShowLegsModel?
    ): Observable<BaseModel<List<ShowLegsModel>>>

    @POST("SurveySequences/SaveSequanceLeg")
    fun updateSequenceLeg(
        @Body body: ShowLegsModel?
    ): Observable<BaseModel<List<ShowLegsModel>>>

    @POST("SurveySequences/SaveSurveySequence")
    fun saveSequenceData(
        @Body body: SaveSequenceModel?
    ): Observable<BaseModel<List<SaveSequenceModel>>>

    @DELETE("Survey/DeleteSurveyPicture/{SurveyPicId}")
    fun deleteSurveyPicture(
        @Path("SurveyPicId") SurveyAddressId: String
    ): Observable<BaseModel<List<PicturesModel>>>

    @POST("SurveyPlanning/SaveSurveyPlanningDetail")
    fun saveSurveyPlanningDetail(@Body body: SurveyPlanningListModel?): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @GET("/api/SurveyPlanning/GetSurveyPlanningListByIds/{SurveyId}/{SequenceId}")
    fun getSurveyPlanningList(
        @Path("SurveyId") surveyId: String,
        @Path("SequenceId") customerId: String
    ): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @GET("SurveyPlanning/GetSurveyPlanningListBySurveyId/{SurveyId}")
    fun getSurveyPlanningListBySurveyId(
        @Path("SurveyId") surveyId: String,
    ): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @DELETE("SurveyPlanning/DeleteSurveyPlanningDetail/{SurveyPlanningDetailId}")
    fun deleteSurveyPlanDetail(
        @Path("SurveyPlanningDetailId") SurveyPlanId: String
    ): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @DELETE("SurveySequences/DeleteSurveySequence/{SurveySequenceId}")
    fun deleteSurveySequence(
        @Path("SurveySequenceId") surveySequenceId: String
    ): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @DELETE("SurveyPlanning/DeleteSurveyPlanningDetailOption/{SurvayId}/{CustomerId}/{ReferenceId}/{OptionId}")
    fun deleteSurveyPlanDetailOption(
        @Path("SurvayId") SurveyId: String,
        @Path("CustomerId") CustomerId: String,
        @Path("ReferenceId") ReferenceId: String,
        @Path("OptionId") OptionId: String
    ): Observable<BaseModel<List<SurveyPlanningListModel>>>

    @GET("SurveyInventory/GetInventoryTypeList")
    fun getInventoryTypeList(): Observable<BaseModel<List<SurveyInventoryList>>>

    @GET("SurveyInventory/GetInventoryItemList/{InventoryTypeId}")
    fun getInventoryItemList(
        @Path("InventoryTypeId") InventoryTypeId: String,
        @Query("SurveyId") SurveyId: String,
    ): Observable<BaseModel<List<InventoryTypeSelectionModel>>>

    @GET("/api/SurveySequences/DeleteSequenceLegById/{SurveySequenceLegId}")
    fun deleteSequenceLegById(
        @Path("SurveySequenceLegId") surveySequenceLegId: String
    ): Observable<BasicModel>

    @GET("SurveyInventory/GetFloorListForDDL")
    fun getInventoryFloorListForDDL(): Observable<BaseModel<List<InventoryFloorModel>>>

    @GET("SurveyInventory/GetRoomListForDDL")
    fun getInventoryRoomListForDDL(@Query("SurveyId") SurveyId: Int): Observable<BaseModel<List<InventoryRoomModel>>>

    @GET("SurveyInventory/GetCountListForDDL")
    fun getInventoryCountListForDDL(): Observable<BaseModel<List<InventoryCountModel>>>

    @Multipart
    @POST("SurveyInventory/SaveSurveyInventory")
    fun postSaveInventoryDetails(
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>?
    ): Observable<SavedSequenceDetailsModel>

    @Multipart
    @POST("SurveyInventory/UpdateSequenceInventoryItem")
    fun postUpdateSequenceInventoryItemDetails(
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>?
    ): Observable<SavedSequenceDetailsModel>


    @GET("SurveySequences/GetSequanceDetails/{surveyId}/{SequenceId}")
    fun getSequenceDetails(
        @Path("surveyId") surveyId: String?,
        @Path("SequenceId") surveySequenceId: String?
    ): Observable<BaseModel<List<SequenceDetailsModel>>>


    @GET("Survey/SubmitSurveyDetailById/{SurveyId}")
    fun saveSequenceDetails(
        @Path("SurveyId") SurveyId: String?
    ): Observable<BaseModel<Boolean>>

    @POST("/api/Survey/SaveSignature")
    fun saveSignature(
        @Body jsonObject: JsonObject
    ): Observable<BaseModel<Any>>

    @GET("/api/Survey/GetSignature/{SurveyId}/{UserId}")
    fun getSignature(
        @Path("SurveyId") SurveyId: String?,
        @Path("UserId") UserId: String?
    ): Observable<BaseModel<String>>

    @GET("/api/Survey/FilterSurveyDetail/{UserId}")
    fun getFilterSurveyDetail(
        @Path("UserId") UserId: String?
    ): Observable<BaseModel<List<FilterSurvey>>>

    @POST("User/ChangePassword/{UserId}/{OldPassword}/{NewPassword}")
    fun postChangePassword(
        @Path("UserId") userId: String?,
        @Path("OldPassword") oldPassword: String?,
        @Path("NewPassword") newPassword: String?
    ): Observable<BasicModel>

    @DELETE("Survey/DeleteSurveyDetail/{SurveyId}/{UserId}/{UpdatedBy}")
    fun deleteSurvey(
        @Path("SurveyId") surveyId: String,
        @Path("UserId") userId: String,
        @Path("UpdatedBy") updatedBy: String
    ): Observable<BaseModel<List<DashboardModel>>>

    @GET("/api/Survey/CopySurvey/{SurveyId}/{UpdatedBy}")
    fun copySurvey(
        @Path("SurveyId") surveyId: String,
        @Path("UpdatedBy") updatedBy: String
    ): Observable<BaseModel<Boolean>>


    @POST("/api/SurveyInventory/SaveUserRoom")
    fun saveRoomName(@Body requestBody: RequestBody): Observable<BaseModel<Boolean>>


    // For Get Activity List In Planning
    @GET("/api/SurveyPlanning/GetSurveyActivity")
    fun getAcitivityListPlanning(): Observable<BaseModel<List<ActivityListPlanningModel>>>

    /**
     * Api used to get code list for planning
     * */
    @GET("/api/SurveyPlanning/GetActivityCodeList")
    fun getCodePlanning(): Observable<BaseModel<List<CodePlanningModel>>>

    @GET("/api/SurveyPlanning/GetActivityTimeList")
    fun getTime(): Observable<BaseModel<List<TimeModel>>>


    /**
     * Api used to get enquiry Type
     * */
    @GET("Survey/GetEnquiryType")
    fun getenquiryType(): Observable<BaseModel<List<EnquiryTypeModel>>>

    /**
     * Api used to get SurveySize
     * */
    @GET("Survey/GetSurveySizeList")
    fun getSurveySize(): Observable<BaseModel<List<SurveySizeListModel>>>


    /**
     * Api used to get Property Type
     * */
    @GET("Survey/GetPropertyTypeList")
    fun getPropertyType(): Observable<BaseModel<List<PropertyTypeModel>>>


    /**
     * Api used to get Property Type List
     * */
    @GET("Survey/GetRentalPropertyList")
    fun getRentalList(): Observable<BaseModel<List<RentalTypeModel>>>


    /**
     * Api used to get address Type
     * */
    @GET("Survey/GetAddressTypeList")
    fun getAddressTypeList(): Observable<BaseModel<List<AddressTypeListModel>>>


    /**
     * Api used to get SurveyInventory By Survey Id
     * */
    @GET("/api/SurveyInventory/GetSurveyInventoryBySurveyId/{SurveyId}")
    fun getSurveyInventoryBySurveyId(@Path("SurveyId") SurveyId: String?): Observable<BaseModel<List<GetSurveyInventoryBySurveyId>>>


    @POST("/api/SurveyOffline/SaveSurveyAddSeqLeg")
    fun saveSequenceOffline(@Query("createdBy") userId: String,@Body body: SyncDataToServer?): Observable<BaseModel<GetAddressAndLegSyncResponse>>

    @POST("/api/SurveyOffline/SaveSurveyPlanPicComment")
    fun savePlanningCommentOffline(@Query("createdBy") userId: String,@Body body: SyncPlanningCommentToServer?): Observable<BaseModel<GetPlanningAndCommentSyncResponse>>

    @POST("/api/SurveyOffline/SaveSurveyInventory_offline")
    fun saveSurveyInventoryOffline(@Query("createdBy") userId: String, @Body body: SyncInventoryToServer?): Observable<BaseModel<Any>>

    @POST("/api/SurveyOffline/DeleteSurveyData_offline")
    fun deleteSurveySyncData(@Query("userId") userId: String,@Body body: JsonElement?): Observable<BaseModel<GetDeleteSurveyDataResponse>>
}
