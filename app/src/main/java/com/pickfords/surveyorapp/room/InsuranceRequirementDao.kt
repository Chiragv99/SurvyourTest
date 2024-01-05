package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.InsuranceRequirementModel

@Dao
interface InsuranceRequirementDao {

    @Query("Select * from insurance_requirement")
    fun getInsuranceRequirementList(): List<InsuranceRequirementModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(insuranceRequirementList: List<InsuranceRequirementModel?>?)

    @Update
    fun update(insuranceRequirementDataList: List<InsuranceRequirementModel?>?)

}