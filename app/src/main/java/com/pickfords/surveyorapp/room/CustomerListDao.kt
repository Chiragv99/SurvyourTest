package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.view.planning.CustomerListModel

@Dao
interface CustomerListDao {

    @Query("Select * from customer_list")
    fun getCustomerList(): List<CustomerListModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(customerList: List<CustomerListModel?>?)

    @Update
    fun update(customerDataList: List<CustomerListModel?>?)

}