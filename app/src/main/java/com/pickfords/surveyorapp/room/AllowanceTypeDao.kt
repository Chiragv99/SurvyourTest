package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.AllowanceTypeModel

@Dao
interface AllowanceTypeDao {

    @Query("Select * from allowance_type")
    fun getAllowanceTypeList(): List<AllowanceTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(allowanceTypeList: List<AllowanceTypeModel?>?)

    @Update
    fun update(allowanceTypeDataList: List<AllowanceTypeModel?>?)

}