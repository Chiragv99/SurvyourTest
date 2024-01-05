package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.enquiry.SurveyTypeModel

@Dao
interface SurveyTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(surveySizeList: List<SurveyTypeModel?>?)

    @Query("Select * from survey_type")
    fun getSurveyType(): List<SurveyTypeModel>
}