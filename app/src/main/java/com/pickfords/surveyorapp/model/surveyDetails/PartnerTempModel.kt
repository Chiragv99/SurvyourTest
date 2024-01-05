package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PartnerTempModel")
class PartnerTempModel {

    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0

    @ColumnInfo(name = "surveyID")
    var surveyID: String = ""

    @ColumnInfo(name = "saveDetails")
    var saveDetails: String = ""

    @ColumnInfo(name = "isSync")
    var isSync: Boolean = false
}