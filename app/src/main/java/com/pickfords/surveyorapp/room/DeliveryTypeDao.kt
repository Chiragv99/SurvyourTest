package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.DeliveryTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceTypeModel

@Dao
interface DeliveryTypeDao {

    @Query("Select * from delivery_type")
    fun getDeliveryTypeList(): List<DeliveryTypeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(deliveryTypeList: List<DeliveryTypeModel?>?)

    @Update
    fun update(deliveryTypeDataList: List<DeliveryTypeModel?>?)

}