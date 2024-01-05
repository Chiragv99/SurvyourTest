package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pickfords.surveyorapp.model.surveyDetails.crateTypeModel

@Dao
interface CrateTypeDao {

    @Query("Select * from crate_type")
    fun getCrateType(): List<crateTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crateType: List<crateTypeModel?>?)

    @Update
    fun update(crateType: List<crateTypeModel?>?)

}