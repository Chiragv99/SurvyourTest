package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.LegListBySequenceIdModel

@Dao
interface LegListDao {

    @Query("Select * from leg_list WHERE surveySequenceId = :surveySequenceId and isDelete = 0")
    fun getLegList(surveySequenceId: String?): List<LegListBySequenceIdModel>

    @Query("Select * from leg_list WHERE surveySequenceId = :surveySequenceId and surveySequenceLegId = :surveySequenceLegId and isDelete = 0")
    fun getLeg(surveySequenceId: String?, surveySequenceLegId: String?): LegListBySequenceIdModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(legList: List<LegListBySequenceIdModel?>?)

    @Insert
    fun insertAllForCopy(legList: List<LegListBySequenceIdModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequenceList: LegListBySequenceIdModel?)

    @Update
    fun updateAll(legList: List<LegListBySequenceIdModel?>?)

    @Update
    fun update(legList: LegListBySequenceIdModel?)

    @Query("DELETE FROM leg_list WHERE surveySequenceLegId = :surveySequenceLegId")
    fun deleteBySurveySequenceLegId(surveySequenceLegId: String)
}