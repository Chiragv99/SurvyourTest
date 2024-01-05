package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.address.PropertyTypeModel

@Dao
interface PropertyTypeDao {

    @Query("Select * from property_type")
    fun getPropertyType(): List<PropertyTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(propertyList: List<PropertyTypeModel?>?)
}