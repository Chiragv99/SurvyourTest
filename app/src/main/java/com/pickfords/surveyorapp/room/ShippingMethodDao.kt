package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.ShippingMethodModel

@Dao
interface ShippingMethodDao {

    @Query("Select * from shipping_method")
    fun getShippingMethodList(): List<ShippingMethodModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shippingMethodList: List<ShippingMethodModel?>?)

    @Update
    fun update(shippingMethodDataList: List<ShippingMethodModel?>?)

}