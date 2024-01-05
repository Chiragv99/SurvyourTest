package com.pickfords.surveyorapp.utils

object AppConstants {
    val ROOM_DB = "pickford-database"
    var surveyID = "0"
    var baseURL = "https://surveyorappapi.pickfords.com/api/"
    var StagingURL = "http://5.77.39.57:50091/api/"
    var LiveURL = "https://surveyorappapi.pickfords.com/api/"
    var TestLiveURL = "https://testsurveyorappapi.pickfords.com/api/"
    var defaultSequenceId : String = "0"
    var notSubmitted : String = "Not Submitted"
    var oldSubmitted : String = "IsOldSubmitted"
    var revisitPlanning = false
    var syncDelay : Long  = 30000
    var cartoonId: String =  "%Carton%"

    // For timeout
    var readTimeOut: Long = 5
    var connectionTimeOut: Long = 5
    var isSubmitted = "1"
    var isSubmittedNot = "0"

    // For Delete Survey Data
    var deleteComment: String = "Comment"
    var deleteInventory: String = "Inventory"
    var deleteSequenceLeg: String = "SequenceLeg"
    var deleteSequence: String = "Sequence"
    var deleteAddress: String = "Address"
    var deletePicture: String = "Picture"
    var deletePlanning: String = "Planning"
    var storeType: String = "Store"
    var wareHouseType: String = "Warehouse"
    var maxCommentLength: Int =  80
    var layoutPadding: Int =  8
}