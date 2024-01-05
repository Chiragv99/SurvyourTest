package com.pickfords.surveyorapp.model.dashboard

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "dashboard")
class DashboardModel : Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyId")
    lateinit var surveyId: String

    @ColumnInfo(name = "EnquiryNo")
    @SerializedName("EnquiryNo")
    var enquiryNo: String? = null

    @ColumnInfo(name = "MoveDate")
    @SerializedName("MoveDate")
    var moveDate: String? = null

    @ColumnInfo(name = "FirstName")
    @SerializedName("FirstName")
    var firstName: String? = null

    @ColumnInfo(name = "MiddleName")
    @SerializedName("MiddleName")
    var middleName: String? = null

    @ColumnInfo(name = "Surname")
    @SerializedName("Surname")
    var surname: String? = null

    @ColumnInfo(name = "Gender")
    @SerializedName("Gender")
    var gender: String? = null

    @ColumnInfo(name = "LanguageId")
    @SerializedName("LanguageId")
    var languageId: Int? = 0

    @ColumnInfo(name = "Latitude")
    @SerializedName("Latitude")
    var latitude: Double? = 0.0

    @ColumnInfo(name = "Longitude")
    @SerializedName("Longitude")
    var longitude: Double? = 0.0

    @ColumnInfo(name = "IsSync")
    @SerializedName("IsSync")
    var isSync: Boolean? = false

    @ColumnInfo(name = "TotalRecords")
    @SerializedName("TotalRecords")
    var totalRecords: Int? = 0

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = 0

    @ColumnInfo(name = "City")
    @SerializedName("City")
    var city: String? = null

    @ColumnInfo(name = "PostalCode")
    @SerializedName("PostalCode")
    var postalCode: String? = null

    @ColumnInfo(name = "Address")
    @SerializedName("Address")
    var address: String? = null

    @ColumnInfo(name = "EmailAddress1") ///EmailAddressP
    @SerializedName("EmailAddress1")
    var emailAddressP: String? = null

    @ColumnInfo(name = "EmailAddress2") //EmailAddressB
    @SerializedName("EmailAddress2")
    var emailAddressB: String? = null

    @ColumnInfo(name = "Mobile2") //PhoneNoP
    @SerializedName("Mobile2")
    var phoneNoP: String? = null

    @ColumnInfo(name = "Mobile1") //MobileNoB
    @SerializedName("Mobile1")
    var mobileNoB: String? = null

    @ColumnInfo(name = "PhoneHome")  //PhoneNoH
    @SerializedName("PhoneHome")
    var phoneNoH: String? = null

    @ColumnInfo(name = "PhoneWork") //PhoneNo
    @SerializedName("PhoneWork")
    var phoneNo: String? = null

    @ColumnInfo(name = "BrokerName")
    @SerializedName("BrokerName")
    var brokerName: String? = null

    @ColumnInfo(name = "CompanyName")
    @SerializedName("CompanyName")
    var companyName: String? = null

    @ColumnInfo(name = "SurveyDate")
    @SerializedName("SurveyDate")
    var surveyDate: String? = null

    @ColumnInfo(name = "SurveyTime")
    @SerializedName("SurveyTime")
    var surveyTime: String? = null

    @ColumnInfo(name = "MoveManager")
    @SerializedName("MoveManager")
    var moveManager: String? = null

    @ColumnInfo(name = "Children")
    @SerializedName("Children")
    var children: Int? = 0

    @ColumnInfo(name = "UserId")
    @SerializedName("UserId")
    var userId: Int? = 0

    @ColumnInfo(name = "IsSubmitted")
    @SerializedName("IsSubmitted")
    var isSubmitted: Boolean = false

    @ColumnInfo(name = "SubmittedDate")
    @SerializedName("SubmittedDate")
    var submittedDate: String? = null

    @ColumnInfo(name = "IsPetTrans")
    @SerializedName("IsPetTrans")
    var isPetTrans: Boolean = false


    @ColumnInfo(name = "EnquiryType")
    @SerializedName("EnquiryType")
    var enquiryType: String? = " "


    @ColumnInfo(name = "OriginCountry")
    @SerializedName("OriginCountry")
    var originCountry: Int? = 0

    @ColumnInfo(name = "IsForEx")
    @SerializedName("IsForEx")
    var isForEx: Boolean = false

    // Change column name and key name based on api
    @ColumnInfo(name = "SurveyType")
    @SerializedName("SurveyType")
    var surveyType: String? = null

    // Change column name and key name based on api
    @ColumnInfo(name = "SurveySize")
    @SerializedName("SurveySize")
    var surveySize: String? = null

    // Change column name and key name based on api
    @ColumnInfo(name = "SurveyComment")
    @SerializedName("SurveyComment")
    var surveyComment: String? = null

    @ColumnInfo(name = "SequenceType")
    @SerializedName("SequenceType")
    var sequenceType: String? = null

    @ColumnInfo(name = "RentalProperty")
    @SerializedName("RentalProperty")
    var rentalProperty: Int? = 0


    @ColumnInfo(name = "IsRevisit")
    @SerializedName("IsRevisit")
    var isRevisit: Boolean? = true


    var isChangedField: Boolean? = false

    var isCompletedSurvey: Boolean? = false

}