package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SequenceModeModel

@Dao
interface SequenceModeDao {

    @Query("Select * from sequence_mode")
    fun getSequenceModeList(): List<SequenceModeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sequenceModeList: List<SequenceModeModel?>?)

    @Update
    fun update(sequenceModeDataList: List<SequenceModeModel?>?)

}