package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel

@Dao
interface SurveyPlanningListDao {

    @Query("Select * from survey_planning_list")
    fun getPlanningList(): List<SurveyPlanningListModel>

    @Query("Select * from survey_planning_list WHERE surveyId = :surveyId")
    fun getPlanningListSurveyId(surveyId: String): List<SurveyPlanningListModel>

    @Query("Select * from survey_planning_list WHERE surveyId = :surveyId AND sequenceId = :surveySequenceId")
    fun getPlanningListBySurveyIdList(
        surveyId: String,
        surveySequenceId: String
    ): List<SurveyPlanningListModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(planningList: List<SurveyPlanningListModel?>?)

    @Insert
    fun insert(planningList: SurveyPlanningListModel?)

    @Update
    fun update(planningDataList: List<SurveyPlanningListModel?>?)

    @Query("SELECT EXISTS(SELECT * FROM survey_planning_list WHERE sequenceId =:surveyPlanningId)")
    fun checkPlanningID(surveyPlanningId: String): Boolean

    @Update
    fun update(planningList: SurveyPlanningListModel?)

    ///update single row
    @Query("UPDATE survey_planning_list SET Options = :options , isChangedField = :isChange WHERE sequenceId = :sequenceId")
    fun updateRow(options: MutableList<SurveyPlanningListModel.Option>?, sequenceId: String,isChange: Boolean = false)

    @Query("DELETE FROM survey_planning_list WHERE surveyPlanningId = :surveyPlanningId")
    fun delete(surveyPlanningId: String)

    @Query("DELETE FROM survey_planning_list")
    fun deleteOption()
}