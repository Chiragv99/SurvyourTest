package com.pickfords.surveyorapp.model.syncDatatoServer

data class SyncDeleteSurveyData(
    val addressList: ArrayList<String> = ArrayList(),
    val sequenceList: ArrayList<String> = ArrayList(),
    val legList: ArrayList<String> = ArrayList(),
    val sequenceInventoryList: ArrayList<String> = ArrayList(),
    val planningList: ArrayList<String> = ArrayList(),
    val pictureList: ArrayList<String> = ArrayList(),
    val partnerList: ArrayList<String> = ArrayList(),
    val commentList: HashMap<String,String> = HashMap(),
    val roomNameList: ArrayList<String> = ArrayList(),
    val partnerContactList: ArrayList<String> = ArrayList(),
    val signatureList: ArrayList<String> = ArrayList(),
    val submitSurveyList: ArrayList<String> = ArrayList(),
) {

    fun clearAllData() {
        addressList.clear()
        sequenceList.clear()
        legList.clear()
        sequenceInventoryList.clear()
        planningList.clear()
        pictureList.clear()
        partnerList.clear()
        commentList.clear()
        roomNameList.clear()
        partnerContactList.clear()
        signatureList.clear()
        submitSurveyList.clear()
    }
}