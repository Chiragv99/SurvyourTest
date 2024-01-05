package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.DistanceUnitTypeModel

@Dao
interface DistanceUnitDao {
    @Query("Select * from distance_type")
    fun getDistanceUnitList(): List<DistanceUnitTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(distanceUnitTypeList: List<DistanceUnitTypeModel?>?)

    @Update
    fun update(distanceUnitTypeDataList: List<DistanceUnitTypeModel?>?)
}