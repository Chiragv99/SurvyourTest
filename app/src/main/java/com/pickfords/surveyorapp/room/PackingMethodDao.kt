package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.PackingMethodModel

@Dao
interface PackingMethodDao {

    @Query("Select * from packing_method")
    fun getPackingMethodList(): List<PackingMethodModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(packingMethodList: List<PackingMethodModel?>?)

    @Update
    fun update(packingMethodDataList: List<PackingMethodModel?>?)

}