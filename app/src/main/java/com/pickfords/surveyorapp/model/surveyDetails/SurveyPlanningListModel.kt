package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "survey_planning_list")
class SurveyPlanningListModel(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("SurveyPlanningId")
    var surveyPlanningId: Int = 0,

    @SerializedName("SurveyId")
    var surveyId: String = "0",

    @SerializedName("SequenceId")
    var sequenceId: String? = null,

//    @ColumnInfo(name = "SurveySequence")
//    @SerializedName("SurveySequence")
//    var surveySequence: String? = "",
    @ColumnInfo(name = "SequenceNo")
    @SerializedName("SequenceNo")
    var sequenceNo: String? = "",

    @ColumnInfo(name = "CustomerId")
    @SerializedName("CustomerId")
    var customerId: String? = "",

    @ColumnInfo(name = "ReferenceId")
    @SerializedName("ReferenceId")
    var referenceId: String? = "",

    @ColumnInfo(name = "ReferenceNumber")
    @SerializedName("ReferenceNumber")
    var referenceNumber: String? = "",

    @ColumnInfo(name = "Options")
    @SerializedName("Options")
    var options: MutableList<Option>? = null,

    @ColumnInfo(name = "CustomerName")
    @SerializedName("CustomerName")
    var customerName: String? = null,

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = 0,

    var isChangedField: Boolean? = false,

) {
    @Entity(tableName = "option")
    class Option(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("OptionId")
        @Expose
        var optionId: Int? = null,


        @ColumnInfo(name = "Days")
        @SerializedName("Days")
        @Expose
        var days: MutableList<Day>? = null
    ) {
        @Entity(tableName = "day")
        class Day(
            @PrimaryKey(autoGenerate = true)
            var surveyPlanningDetailId1: Int? = 0,

            @SerializedName("SurveyPlanningDetailId")
            var surveyPlanningDetailId: Int? = 0,

            @ColumnInfo(name = "OptionDay")
            @SerializedName("OptionDay")
            var optionDay: Int? = null,

            @ColumnInfo(name = "AM")
            @SerializedName("AM")
            var am: String? = null,

            @ColumnInfo(name = "NoOfDriver") //AMDriver
            @SerializedName("NoOfDriver")
            var aMDriver: String? = null,

            @ColumnInfo(name = "NoOfPorter") //AMPorter
            @SerializedName("NoOfPorter")
            var aMPorter: String? = null,

            @ColumnInfo(name = "PM")
            @SerializedName("PM")
            var pm: String? = null,

            @ColumnInfo(name = "PMDriver")
            @SerializedName("PMDriver")
            var pMDriver: String? = "",

            @ColumnInfo(name = "PMPorter")
            @SerializedName("PMPorter")
            var pMPorter: String? = null,

            @ColumnInfo(name = "Time")
            @SerializedName("Time")
            var time: String? = "",

            @ColumnInfo(name = "Tracking")
            @SerializedName("Tracking")
            var tracking: Boolean? = null,

            @ColumnInfo(name = "AddressId")
            @SerializedName("AddressId")
            var addressId: Int? = null,

            @ColumnInfo(name = "AddressName")
            @SerializedName("AddressName")
            var address: String? = null,

            @ColumnInfo(name = "ActivityId")
            @SerializedName("ActivityId")
            var activityId: Int? = null,

            @ColumnInfo(name = "Activity")
            @SerializedName("Activity")
            var activity: String? = "",

            @ColumnInfo(name = "Code")
            @SerializedName("Code")
            var code: String? = "",

            @ColumnInfo(name = "Notes")
            @SerializedName("Notes")
            var notes: String? = null,

            var isStaticData: Boolean? = false,

            var tempAddressId: String = "",
        )
    }
}
