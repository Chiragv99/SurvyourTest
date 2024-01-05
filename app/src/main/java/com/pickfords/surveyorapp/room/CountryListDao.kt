package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.address.CountryListModel

@Dao
interface CountryListDao {
    @Query("Select * from country_list")
    fun getCountryList(): List<CountryListModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(countryList: List<CountryListModel?>?)

    @Update
    fun update(countryDataList: List<CountryListModel?>?)

    @Query("Select * from country_list where countryId = :id limit 1")
    fun getCountryNameFromId(id: Int): CountryListModel
}