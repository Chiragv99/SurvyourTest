package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.planning.TimeModel

@Dao
interface TimeListDao {
    @Query("Select * from time_list")
    fun getTimeList(): List<TimeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(timeList: List<TimeModel?>?)

}