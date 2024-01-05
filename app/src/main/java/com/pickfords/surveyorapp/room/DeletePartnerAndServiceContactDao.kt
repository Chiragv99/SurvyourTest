package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.DeletePartnerAndServiceContactModel
import com.pickfords.surveyorapp.model.surveyDetails.CommentsDetailModel

@Dao
interface DeletePartnerAndServiceContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deleteContact: DeletePartnerAndServiceContactModel?)


    @Query("Select * from deletePartnerAndService")
    fun getDeletePartnerList(): List<DeletePartnerAndServiceContactModel>


    @Query("DELETE FROM deletePartnerAndService WHERE partnerId = :partnerId")
    fun delete(partnerId: String)


}