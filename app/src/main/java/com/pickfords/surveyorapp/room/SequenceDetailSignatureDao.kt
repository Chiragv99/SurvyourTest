package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.surveyDetails.SeqeunceDetailSignature
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel

@Dao
interface SequenceDetailSignatureDao {

    @Query("Select * from sequence_signature")
    fun getSignature(): List<SeqeunceDetailSignature>

    @Query("Select * from sequence_signature WHERE surveyId = :surveyId AND surveySequenceId = :sequenceId  ORDER BY id DESC  LIMIT 1")
    fun getSignatureById(surveyId: String,sequenceId: String):  SeqeunceDetailSignature


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(signature : List<SeqeunceDetailSignature?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(signature: SeqeunceDetailSignature?)


    @Query("DELETE FROM sequence_signature WHERE  surveyId = :surveyId")
    fun delete(surveyId: String)

    @Query("DELETE FROM sequence_signature WHERE  surveyId = :surveyId AND surveySequenceId = :surveySequenceId")
    fun deleteBySequenceId(surveyId: String,surveySequenceId: String)


    @Query("Select * from sequence_signature WHERE isOffline = :offline")
    fun getSignatureByIdOffline(offline: Boolean):   List<SeqeunceDetailSignature>

    @Query("Select * from sequence_signature WHERE isOffline = :offline AND surveyId = :surveyId ")
    fun getSignatureByIdOfflineBySurveyId(offline: Boolean,surveyId: String):   List<SeqeunceDetailSignature>


    @Query("Select * from sequence_signature WHERE  surveyId = :surveyId AND surveySequenceId = :surveySequenceId")
    fun getSignatureByIdOfflineIsExist(surveyId: String,surveySequenceId: String):   List<SeqeunceDetailSignature>


    @Query("Select * from sequence_signature where SurveyId = :surveyId")
    fun getSignatureSizeBySurveyId(surveyId: String):  List<SeqeunceDetailSignature>

    @Query("Select * from sequence_signature WHERE surveyId = :surveyId AND isVideoSequence = :isVideoSurvey  ORDER BY id DESC  LIMIT 1")
    fun getIsVideoSurvey(surveyId: String,isVideoSurvey: Boolean):   List<SeqeunceDetailSignature>

}