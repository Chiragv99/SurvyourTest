package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SequenceGroupModel

@Dao
interface SequenceGroupDao {

    @Query("Select * from sequence_group")
    fun getSequenceGroupList(): List<SequenceGroupModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sequenceGroupList: List<SequenceGroupModel?>?)

    @Update
    fun update(sequenceGroupDataList: List<SequenceGroupModel?>?)

}