package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SequenceTypeModel

@Dao
interface SequenceTypeDao {

    @Query("Select * from sequence_type")
    fun getSequenceTypeList(): List<SequenceTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sequenceTypeList: List<SequenceTypeModel?>?)

    @Update
    fun update(sequenceTypeDataList: List<SequenceTypeModel?>?)

}