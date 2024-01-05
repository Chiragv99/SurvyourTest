package com.pickfords.surveyorapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.ActivityListPlanningModel

@Dao
interface  EnquiryListDao {

    @Query("Select * from enquiry_type")
    fun getEnquiryTypeList(): List<EnquiryTypeModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(enquiryList: List<EnquiryTypeModel?>?)
}