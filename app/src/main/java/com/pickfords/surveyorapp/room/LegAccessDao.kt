package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.LegAccessModel

@Dao
interface LegAccessDao {

    @Query("Select * from leg_access")
    fun getLegAccessList(): List<LegAccessModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(legAccessList: List<LegAccessModel?>?)

    @Update
    fun update(legAccessDataList: List<LegAccessModel?>?)

}