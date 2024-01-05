package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.DeliveryTypeModel

@Dao
interface DeletePlanningDataDao {

    @Query("Select * from deleteplanningdata")
    fun getDeletePlanningDataList(): List<DeletePlanningData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deleteplanning: DeletePlanningData)

}