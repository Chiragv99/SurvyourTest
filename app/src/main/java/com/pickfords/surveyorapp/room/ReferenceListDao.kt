package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.view.planning.ReferenceListModel

@Dao
interface ReferenceListDao {

    @Query("Select * from reference_list")
    fun getReferenceList(): List<ReferenceListModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(referenceList: List<ReferenceListModel?>?)

    @Update
    fun update(referenceDataList: List<ReferenceListModel?>?)

}