package com.pickfords.surveyorapp.model.syncDatatoServer

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class SyncAddress {

    @SerializedName("SurveyAddressId")
    @Expose
    private var surveyAddressId: Int? = null

    @SerializedName("SurveyId")
    @Expose
    private var surveyId: Int? = null

    @SerializedName("TitleName")
    @Expose
    private var titleName: String? = null

    @SerializedName("AddressOne")
    @Expose
    private var addressOne: String? = null

    @SerializedName("AddressTwo")
    @Expose
    private var addressTwo: String? = null

    @SerializedName("CityName")
    @Expose
    private var cityName: String? = null

    @SerializedName("Postcode")
    @Expose
    private var postcode: String? = null

    @SerializedName("County")
    @Expose
    private var county: String? = null

    @SerializedName("CountryId")
    @Expose
    private var countryId: Int? = null

    @SerializedName("CreatedBy")
    @Expose
    private var createdBy: Int? = null

    @SerializedName("UserId")
    @Expose
    private var userId: Int? = null

    @SerializedName("CountryCode")
    @Expose
    private var countryCode: String? = null

    @SerializedName("LineNo")
    @Expose
    private var lineNo: Int? = null

    @SerializedName("CountryName")
    @Expose
    private var countryName: String? = null

    @SerializedName("AddressTypeId")
    @Expose
    private var addressTypeId: Int? = null

    @SerializedName("Floor")
    @Expose
    private var floor: String? = null

    @SerializedName("Access")
    @Expose
    private var access: String? = null

    @SerializedName("Stairs")
    @Expose
    private var stairs: String? = null

    @SerializedName("DistanceUnitId")
    @Expose
    private var distanceUnitId: Int? = null

    @SerializedName("Distance")
    @Expose
    private var distance: String? = null

    @SerializedName("Location")
    @Expose
    private var location: String? = null

    @SerializedName("Lift")
    @Expose
    private var lift: Boolean? = null

    @SerializedName("IsLongCarry")
    @Expose
    private var isLongCarry: Boolean? = null

    @SerializedName("IsShittle")
    @Expose
    private var isShittle: Boolean? = null

    @SerializedName("PakringPermit")
    @Expose
    private var pakringPermit: Boolean? = null

    @SerializedName("ParkingPermitTypeId")
    @Expose
    private var parkingPermitTypeId: Int? = null

    @SerializedName("IsNew")
    @Expose
    private var isNew: Boolean? = null

    @SerializedName("AddressTypeCode")
    @Expose
    private var addressTypeCode: String? = null

    fun getSurveyAddressId(): Int? {
        return surveyAddressId
    }

    fun setSurveyAddressId(surveyAddressId: Int?) {
        this.surveyAddressId = surveyAddressId
    }

    fun getSurveyId(): Int? {
        return surveyId
    }

    fun setSurveyId(surveyId: Int?) {
        this.surveyId = surveyId
    }

    fun getTitleName(): String? {
        return titleName
    }

    fun setTitleName(titleName: String?) {
        this.titleName = titleName
    }

    fun getAddressOne(): String? {
        return addressOne
    }

    fun setAddressOne(addressOne: String?) {
        this.addressOne = addressOne
    }

    fun getAddressTwo(): String? {
        return addressTwo
    }

    fun setAddressTwo(addressTwo: String?) {
        this.addressTwo = addressTwo
    }

    fun getCityName(): String? {
        return cityName
    }

    fun setCityName(cityName: String?) {
        this.cityName = cityName
    }

    fun getPostcode(): String? {
        return postcode
    }

    fun setPostcode(postcode: String?) {
        this.postcode = postcode
    }

    fun getCounty(): String? {
        return county
    }

    fun setCounty(county: String?) {
        this.county = county
    }

    fun getCountryId(): Int? {
        return countryId
    }

    fun setCountryId(countryId: Int?) {
        this.countryId = countryId
    }

    fun getCreatedBy(): Int? {
        return createdBy
    }

    fun setCreatedBy(createdBy: Int?) {
        this.createdBy = createdBy
    }

    fun getUserId(): Int? {
        return userId
    }

    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getLineNo(): Int? {
        return lineNo
    }

    fun setLineNo(lineNo: Int?) {
        this.lineNo = lineNo
    }

    fun getCountryName(): String? {
        return countryName
    }

    fun setCountryName(countryName: String?) {
        this.countryName = countryName
    }

    fun getAddressTypeId(): Int? {
        return addressTypeId
    }

    fun setAddressTypeId(addressTypeId: Int?) {
        this.addressTypeId = addressTypeId
    }

    fun getFloor(): String? {
        return floor
    }

    fun setFloor(floor: String?) {
        this.floor = floor
    }

    fun getAccess(): String? {
        return access
    }

    fun setAccess(access: String?) {
        this.access = access
    }

    fun getStairs(): String? {
        return stairs
    }

    fun setStairs(stairs: String?) {
        this.stairs = stairs
    }

    fun getDistanceUnitId(): Int? {
        return distanceUnitId
    }

    fun setDistanceUnitId(distanceUnitId: Int?) {
        this.distanceUnitId = distanceUnitId
    }

    fun getDistance(): String? {
        return distance
    }

    fun setDistance(distance: String?) {
        this.distance = distance
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getLift(): Boolean? {
        return lift
    }

    fun setLift(lift: Boolean?) {
        this.lift = lift
    }

    fun getIsLongCarry(): Boolean? {
        return isLongCarry
    }

    fun setIsLongCarry(isLongCarry: Boolean?) {
        this.isLongCarry = isLongCarry
    }

    fun getIsShittle(): Boolean? {
        return isShittle
    }

    fun setIsShittle(isShittle: Boolean?) {
        this.isShittle = isShittle
    }

    fun getPakringPermit(): Boolean? {
        return pakringPermit
    }

    fun setPakringPermit(pakringPermit: Boolean?) {
        this.pakringPermit = pakringPermit
    }

    fun getParkingPermitTypeId(): Int? {
        return parkingPermitTypeId
    }

    fun setParkingPermitTypeId(parkingPermitTypeId: Int?) {
        this.parkingPermitTypeId = parkingPermitTypeId
    }

    fun getIsNew(): Boolean? {
        return isNew
    }

    fun setIsNew(isNew: Boolean?) {
        this.isNew = isNew
    }

    fun getAddressTypeCode(): String? {
        return addressTypeCode
    }

    fun setAddressTypeCode(addressTypeCode: String?) {
        this.addressTypeCode = addressTypeCode
    }

}