package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.surveyDetails.SyncDataModel

@Dao
interface SyncDao {
    @Query("Select * from sync_data")
    fun getSyncData(): List<SyncDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(syncDataModel: SyncDataModel)

    @Query("DELETE FROM sync_data")
    fun delete()
}