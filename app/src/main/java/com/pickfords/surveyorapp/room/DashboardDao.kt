package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.dashboard.DashboardModel

@Dao
interface DashboardDao {

    /* @Query("Select * from dashboard WHERE isCompletedSurvey=:isCompletedSurvey")
     fun getDashboardList(isCompletedSurvey:Boolean=false): List<DashboardModel>*/

    @Query("Select * from dashboard")
    fun getDashboardList(): List<DashboardModel>

    @Query("Select * from dashboard WHERE surveyId = :surveyId")
    fun getDashboardListBySurveyId(surveyId: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted")
    fun getNotDashboardList(submitted: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE surveyId=:surveyId")
    fun getDashboard(surveyId: String?): DashboardModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(dashboardList: List<DashboardModel?>?)

    @Update
    fun updateAll(dashboardDataList: List<DashboardModel?>?)

    @Update
    fun update(dashboardDataList: DashboardModel?)

    @Query("DELETE FROM dashboard WHERE surveyId = :surveyId")
    fun deleteBySurveyId(surveyId: String)


    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate AND  SurveyDate > DateTime(:currentDate) AND UserId = :userid ORDER BY SurveyDate ASC,  SurveyTime ASC" )
    fun getsubmittedData(submitted: String,surveyDate: String,currentDate: String,userid: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY SurveyDate ASC,  SurveyTime ASC" )
    fun getsubmittedData(submitted: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted  AND  SurveyDate > DateTime(:currentDate) ORDER BY SurveyDate ASC,  SurveyTime ASC" )
    fun getsubmittedDataAll(submitted: String,currentDate: String): List<DashboardModel>



    // Without Date Selection
    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY enquiryNo ASC" )
    fun getsubmittedDataAllEnquirySmallToLargest(submitted: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY enquiryNo DESC" )
    fun getsubmittedDataAllEnquiryLargestToSmall(submitted: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY FirstName ASC" )
    fun getsubmittedDataByFirstName(submitted: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY MoveManager ASC" )
    fun getsubmittedDataByMoveManager(submitted: String): List<DashboardModel>


    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY SurveyDate DESC" )
    fun getsubmittedDataAllSurveyDateOldToNewest(submitted: String): List<DashboardModel>


    @Query("Select * from dashboard WHERE IsSubmitted = :submitted ORDER BY SurveyDate ASC" )
    fun getsubmittedDataAllSurveyDatNewestToOld(submitted: String): List<DashboardModel>




    // With Date Selection
    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate ORDER BY enquiryNo ASC" )
    fun getsubmittedDataAllEnquirySmallToLargest(submitted: String,surveyDate: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate  ORDER BY enquiryNo DESC" )
    fun getsubmittedDataAllEnquiryLargestToSmall(submitted: String,surveyDate: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate ORDER BY FirstName ASC" )
    fun getsubmittedDataByFirstName(submitted: String,surveyDate: String): List<DashboardModel>

    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate ORDER BY MoveManager ASC" )
    fun getsubmittedDataByMoveManager(submitted: String,surveyDate: String): List<DashboardModel>


    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate ORDER BY SurveyDate DESC, SurveyTime DESC" )
    fun getsubmittedDataAllSurveyDateOldToNewest(submitted: String,surveyDate: String): List<DashboardModel>


    @Query("Select * from dashboard WHERE IsSubmitted = :submitted AND surveyDate = :surveyDate ORDER BY SurveyDate ASC,  SurveyTime ASC" )
    fun getsubmittedDataAllSurveyDatNewestToOld(submitted: String,surveyDate: String): List<DashboardModel>


    @Query("select * from dashboard  WHERE IsSubmitted = :submitted AND UserId = :userid ORDER BY [SurveyDate] DESC")
    fun getNotSubmittedData(submitted: String,userid: String): List<DashboardModel>

}