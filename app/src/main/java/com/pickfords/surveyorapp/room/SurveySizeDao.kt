package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel


@Dao
interface SurveySizeDao {

    @Query("Select * from survey_size")
    fun getSurveySizeList(): List<SurveySizeListModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(surveySizeList: List<SurveySizeListModel?>?)
}