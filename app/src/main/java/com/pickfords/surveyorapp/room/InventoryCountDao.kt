package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.InventoryCountModel

@Dao
interface InventoryCountDao {

    @Query("Select * from inventory_count")
    fun getInsuranceCountList(): List<InventoryCountModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(insuranceCountList: List<InventoryCountModel?>?)

    @Update
    fun update(insuranceCountDataList: List<InventoryCountModel?>?)

}