package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.surveyDetails.PartnerTempModel

@Dao
interface PartnerAndServiceTempDao {
    @Query("Select * from PartnerTempModel")
    fun getPartnerTempList(): List<PartnerTempModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tempList: PartnerTempModel?)

    @Query("UPDATE PartnerTempModel SET saveDetails = :saveDetails WHERE surveyID = :surveyID")
    fun update(saveDetails: String, surveyID: String)

    @Query("DELETE FROM PartnerTempModel WHERE Id = :id")
    fun deleteById(id: Int)

    @Query("Select * from PartnerTempModel WHERE isSync = :isSync  order by Id desc LIMIT 1")
    fun getPartnerTempListOffline(isSync: String): PartnerTempModel

    @Query("Select * from PartnerTempModel WHERE isSync = :isSync AND surveyID = :surveyId order by Id desc LIMIT 1")
    fun getPartnerTempListOfflineBySurveyId(isSync: String, surveyId: String): PartnerTempModel

    @Query("Select surveyID from PartnerTempModel WHERE surveyID = :surveyId  order by Id desc LIMIT 1")
    fun getPartnerTempListBySurveyOffline(surveyId: String): String

    @Query("DELETE FROM PartnerTempModel")
    fun deletePartnerTable()

}