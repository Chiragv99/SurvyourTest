package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.model.PartnerServiceModel
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel

@Dao
interface PartnerAndServiceDao {
    @Query("Select * from partnerListModel")
    fun getPartnerList(): List<PartnerListModel>

    @Query("Select * from AdditionalInfoPartnerModel")
    fun getAdditionalPartnerList(): List<AdditionalInfoPartnerModel>

    @Query("Select * from save_sequence WHERE surveyId = :surveyId AND isDelete=:isDelete")
    fun getSequenceListBySurveyId(
        surveyId: String,
        isDelete: Boolean = false
    ): List<SaveSequenceModel>

    @Query("Select * from save_sequence WHERE surveySequenceId = :surveySequenceId")
    fun getSequence(surveySequenceId: String): SaveSequenceModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(additionalList: List<AdditionalInfoPartnerModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPartnerList(additionalList: List<PartnerListModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequenceList: SaveSequenceModel?)

    @Update
    fun updateAllPartnerList(partnerList: List<PartnerListModel?>?)

    @Update
    fun updateAllAdditionalPartnerList(partnerList: List<AdditionalInfoPartnerModel?>?)

    @Update
    fun update(commentsDataList: SaveSequenceModel?)

    @Query("DELETE FROM save_sequence WHERE surveySequenceId = :sequenceID")
    fun deleteBySequenceID(sequenceID: String)

    @Query("DELETE FROM save_sequence WHERE surveyId = :surveyId")
    fun deleteBySurveyID(surveyId: String)
}