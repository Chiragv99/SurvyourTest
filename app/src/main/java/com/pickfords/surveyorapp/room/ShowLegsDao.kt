package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel

@Dao
interface ShowLegsDao {

    @Query("Select * from show_legs WHERE isDelete = 0")
    fun getShowLegsList(): List<ShowLegsModel>
  @Query("Select * from show_legs")
    fun getShowLegsListAll(): List<ShowLegsModel>

    @Query("Select * from show_legs WHERE surveySequenceId = :surveySequenceId and isDelete = 0")
    fun getLegList(surveySequenceId: String?): List<ShowLegsModel>

     @Query("Select * from show_legs WHERE surveyId = :surveyId and isDelete = 0")
    fun getLegListBySurvey(surveyId: String?): List<ShowLegsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(showLegsList: List<ShowLegsModel?>?)

    @Query("SELECT * FROM show_legs Where SurveySequenceId IN (SELECT SurveySequenceId FROM save_sequence Where SurveyId = :surveyID)")
    fun getLegBySurvey(surveyID: String): List<ShowLegsModel>

    @Query("Select * from show_legs WHERE surveySequenceId = :surveySequenceId and surveySequenceLegId = :surveySequenceLegId and isDelete = 0")
    fun getLeg(surveySequenceId: String?, surveySequenceLegId: String?): ShowLegsModel

    @Insert
    fun insertAllForCopy(showLegsList: ShowLegsModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(showLegsList: ShowLegsModel?)

    @Query("Select * from show_legs WHERE surveySequenceId = :surveySequenceId and isDelete = 0 GROUP BY surveySequenceLeg ORDER BY SurveySequenceLeg")
    fun getShowLegsBySurveySequenceId(surveySequenceId: String): List<ShowLegsModel>

    @Update
    fun updateAll(showLegsDataList: List<ShowLegsModel?>?)

    @Update
    fun update(showLegsDataList: ShowLegsModel?)


    @Query("DELETE FROM show_legs WHERE surveySequenceLegId = :surveySequenceLegId")
    fun delete(surveySequenceLegId: String)

    @Query("UPDATE  show_legs SET OriginAddress = :originAddress WHERE OriginAddressId =:originAddressId")
    fun updateLegAddress(originAddress: String?, originAddressId: String?)

    @Query("UPDATE  show_legs SET  DestinationAddress = :destinationAddress WHERE DestinationAddressId =:destinationAddressId")
    fun updateLegDestinationAddress(destinationAddress: String?, destinationAddressId: String?)

  @Query("Select * from show_legs WHERE SurveySequenceLegId = :surveySequenceLegId")
  fun getLegBySurveySequenceLegId(surveySequenceLegId: String): ShowLegsModel


  @Query(
    "Select sd.SurveySequenceLegId from show_legs AS sd " +
            "WHERE  OriginAddressId = :addressId  OR  DestinationAddressId = :addressId")
  fun getAddressIsUsedInLeg(addressId: String): String


  @Query("UPDATE  show_legs SET  DestinationAddress = :destinationAddress ,DestinationAddressId = :destinationAddressId WHERE DestinationAddressId =:destinationAddressId")
  fun updateDestinationAddressTitle(destinationAddress: String?, destinationAddressId: String?)


  @Query("UPDATE  show_legs SET  OriginAddress = :originAddress ,OriginAddressId = :originAddressId WHERE OriginAddressId =:originAddressId")
  fun updateOriginAddressTitle(originAddress: String?, originAddressId: String?)

}