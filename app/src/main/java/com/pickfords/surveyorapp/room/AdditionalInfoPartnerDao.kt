package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel

@Dao
interface AdditionalInfoPartnerDao {

    @Query("Select * from additionalinfopartnermodel WHERE SurveyId = :surveyId")
    fun getAdditionlInfo(surveyId: String): List<AdditionalInfoPartnerModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(additionalList: List<AdditionalInfoPartnerModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(additionalList: List<AdditionalInfoPartnerModel?>?)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(additionalList: AdditionalInfoPartnerModel?)

    @Query("DELETE FROM additionalinfopartnermodel WHERE Id = :surveyAddressId")
    fun delete(surveyAddressId: String)

    @Query("Select * from additionalinfopartnermodel WHERE IsNew = :isNew")
    fun getOfflinePartners(isNew: String): List<AdditionalInfoPartnerModel>
//
//    @Query("Select sd.Id from additionalinfopartnermodel AS sd " + "WHERE FirstName = :firstName")
//    fun getPartnerIsAvailable(firstName: String): String

}