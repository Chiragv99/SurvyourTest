package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.surveyDetails.PermitTypeListModel

@Dao
interface PermitTypeListModelDao {

    @Query("Select * from permit_type_list")
    fun getPermitTypeList(): List<PermitTypeListModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<PermitTypeListModel?>?)


}