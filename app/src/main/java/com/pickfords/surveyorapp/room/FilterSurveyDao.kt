package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.network.BaseModel


@Dao
interface FilterSurveyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(filterSurveyList: List<FilterSurvey?>?)

    @Insert()
    fun insert(filterSurveyList: FilterSurvey)

    @Query("Select * from filtersurvey")
    fun getFilterSurveyList(): List<FilterSurvey>



}