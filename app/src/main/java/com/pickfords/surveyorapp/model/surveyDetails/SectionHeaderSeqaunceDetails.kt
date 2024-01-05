package com.pickfords.surveyorapp.model.surveyDetails

data class SectionHeaderSeqaunceDetails(val  fromCity:String, val toCity:String, val headerName: String):SequenceDetailViewTypeModel {
    override fun getViewType(): Int = 0

}