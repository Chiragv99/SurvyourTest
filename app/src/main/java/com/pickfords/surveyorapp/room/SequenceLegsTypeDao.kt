package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SequenceLegsTypeModel

@Dao
interface SequenceLegsTypeDao {

    @Query("Select * from sequence_legs_type")
    fun getSequenceLegsTypeList(): List<SequenceLegsTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sequenceLegsTypeList: List<SequenceLegsTypeModel?>?)

    @Update
    fun update(sequenceLegsTypeDataList: List<SequenceLegsTypeModel?>?)

}