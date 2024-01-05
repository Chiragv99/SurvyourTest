package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.surveyDetails.PermitTypeListModel

@Dao
interface AddressTypeListModelDao {

    @Query("Select * from address_type_list")
    fun getAddressTypeList(): List<AddressTypeListModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<AddressTypeListModel?>?)


}