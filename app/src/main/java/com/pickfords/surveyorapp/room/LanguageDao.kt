package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.LanguageModel

@Dao
interface LanguageDao {

    @Query("Select * from language_list")
    fun getLanguageList(): List<LanguageModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(languageList: List<LanguageModel?>?)

    @Update
    fun update(languageDataList: List<LanguageModel?>?)

}