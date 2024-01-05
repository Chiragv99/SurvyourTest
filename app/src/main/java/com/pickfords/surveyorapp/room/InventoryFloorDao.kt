package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.InsuranceRequirementModel
import com.pickfords.surveyorapp.model.surveyDetails.InventoryFloorModel

@Dao
interface InventoryFloorDao {

    @Query("Select * from inventory_floor")
    fun getInsuranceFloorList(): List<InventoryFloorModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(insuranceFloorList: List<InventoryFloorModel?>?)

    @Update
    fun update(insuranceFloorDataList: List<InventoryFloorModel?>?)

}