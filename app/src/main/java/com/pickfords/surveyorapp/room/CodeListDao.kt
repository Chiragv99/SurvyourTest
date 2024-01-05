package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.surveyDetails.CodePlanningModel

@Dao
interface CodeListDao {

    @Query("Select * from code_list")
    fun getCodeList(): List<CodePlanningModel>

    @Query("Select discription  from code_list where activityId = :activityId")
    fun getCodeByActivityId(activityId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(codeList: List<CodePlanningModel?>?)


}