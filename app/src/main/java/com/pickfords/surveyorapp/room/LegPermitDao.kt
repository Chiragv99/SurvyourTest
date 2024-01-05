package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.LegPermitModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceTypeModel

@Dao
interface LegPermitDao {

    @Query("Select * from leg_permit")
    fun getLegPermitList(): List<LegPermitModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(legPermitList: List<LegPermitModel?>?)

    @Update
    fun update(legPermitDataList: List<LegPermitModel?>?)

}