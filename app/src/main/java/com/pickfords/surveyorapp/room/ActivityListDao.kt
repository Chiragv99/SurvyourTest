package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.surveyDetails.ActivityListPlanningModel

@Dao
interface ActivityListDao {

    @Query("Select * from activity_list")
    fun getActivityList(): List<ActivityListPlanningModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(addressList: List<ActivityListPlanningModel?>?)


}