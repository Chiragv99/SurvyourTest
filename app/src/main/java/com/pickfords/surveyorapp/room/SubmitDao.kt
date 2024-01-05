package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.surveyDetails.SubmitDataModel

@Dao
interface SubmitDao {
    @Query("Select * from submit_data")
    fun getSubmitData(): List<SubmitDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(syncDataModel: SubmitDataModel)

    @Query("DELETE FROM submit_data WHERE surveyId = :surveyId")
    fun delete(surveyId: String)
}