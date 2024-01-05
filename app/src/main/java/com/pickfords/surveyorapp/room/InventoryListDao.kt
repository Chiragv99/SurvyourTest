package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SurveyInventoryList

@Dao
interface InventoryListDao {

    @Query("Select * from survey_inventory_list")
    fun getInventoryList(): List<SurveyInventoryList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(insuranceRoomList: List<SurveyInventoryList?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(insuranceRoomList: SurveyInventoryList)


    @Update
    fun update(insuranceRoomDataList: List<SurveyInventoryList?>?)

}