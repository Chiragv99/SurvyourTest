package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel

@Dao
interface SaveSequenceDao {

    @Query("Select * from save_sequence  ORDER BY surveySequence")
    fun getSequenceList(): List<SaveSequenceModel>

    @Query("Select * from save_sequence WHERE surveyId = :surveyId")
    fun getSequenceListBySurveyId(surveyId: String): List<SaveSequenceModel>

    @Query("Select * from save_sequence WHERE surveyId = :surveyId AND isDelete=:isDelete")
    fun getSequenceListBySurveyId(
        surveyId: String,
        isDelete: Boolean = false
    ): List<SaveSequenceModel>

    @Query("Select * from save_sequence WHERE surveySequence = :surveySequence AND isDelete=:isDelete")
    fun getSequenceListBySurveyName(
        surveySequence: String,
        isDelete: Boolean = false
    ): List<SaveSequenceModel>

    /**
     * Updating only amount and price
     * By order id
     */
    @Query("UPDATE  save_sequence SET OriginAddress = :originAddress, OriginAddressId = :originAddressId WHERE OriginAddressId =:originAddressId")
    fun updateAddress(originAddress: String?, originAddressId: String?)

    @Query("UPDATE  save_sequence SET  DestinationAddress = :destinationAddress ,DestinationAddressId = :destinationAddressId WHERE DestinationAddressId =:destinationAddressId")
    fun updateDestinationAddress(destinationAddress: String?, destinationAddressId: String?)

    @Query("Select * from save_sequence WHERE surveySequenceId = :surveySequenceId")
    fun getSequence(surveySequenceId: String): SaveSequenceModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(sequenceList: List<SaveSequenceModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequenceList: SaveSequenceModel?)

    @Update
    fun updateAll(sequenceDataList: List<SaveSequenceModel?>?)

    @Update
    fun update(commentsDataList: SaveSequenceModel?)

    @Query("DELETE FROM save_sequence WHERE surveySequenceId = :sequenceID")
    fun deleteBySequenceID(sequenceID: String)

    @Query("DELETE FROM save_sequence WHERE surveyId = :surveyId")
    fun deleteBySurveyID(surveyId: String)

    @Query("SELECT SurveySequence FROM save_sequence WHERE surveyId = :surveyId ORDER BY SurveySequence DESC LIMIT 1")
    fun getLatestSequenceName(surveyId: String): String

    @Query("Select * from save_sequence WHERE SurveyId =:surveyId and surveySequenceId = :surveySequenceId")
    fun getSequenceBySurveyIdAndSequenceId(surveyId: String, surveySequenceId: String): SaveSequenceModel



    @Query(
        "Select sd.surveySequenceId from save_sequence AS sd " +
                "WHERE  OriginAddressId = :addressId  OR  DestinationAddressId = :addressId")
    fun getAddressIsUsedInSequence(addressId: String): String


    @Query("UPDATE  save_sequence SET  DestinationAddress = :destinationAddress ,DestinationAddressId = :destinationAddressId, isChangedField = 1 WHERE DestinationAddressId =:destinationAddressId")
    fun updateDestinationAddressTitle(destinationAddress: String?, destinationAddressId: String?)


    @Query("UPDATE  save_sequence SET  OriginAddress = :originAddress ,OriginAddressId = :originAddressId, isChangedField = 1 WHERE OriginAddressId =:originAddressId")
    fun updateOriginAddressTitle(originAddress: String?, originAddressId: String?)



}