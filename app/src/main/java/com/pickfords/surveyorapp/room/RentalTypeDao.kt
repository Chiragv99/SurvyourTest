package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel

@Dao
interface RentalTypeDao {

    @Query("Select * from rental_type")
    fun getRentalList(): List<RentalTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(propertyList: List<RentalTypeModel?>?)
}