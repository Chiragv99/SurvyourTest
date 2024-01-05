package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel

@Dao
interface InventoryItemListDao {

    @Query("Select * from inventory_type_selection GROUP BY Name")
    fun getInventoryList(): List<InventoryTypeSelectionModel>

//    @Query("Select * from inventory_type_selection WHERE inventoryTypeId = :inventoryTypeId GROUP BY Name")
    @Query("SELECT * FROM inventory_type_selection WHERE InventoryTypeId  =  :inventoryTypeId and InventoryItemId Is not null AND SurveyId IS NULL")
    fun getInventoryListById(inventoryTypeId: String): List<InventoryTypeSelectionModel>


    @Query("SELECT * FROM inventory_type_selection WHERE name  =  :inventoryName")
    fun getInventoryListByName(inventoryName: String): List<InventoryTypeSelectionModel>



    @Query("Select * from inventory_type_selection WHERE InventoryTypeId  =  :inventoryTypeId and surveyId = :surveyId GROUP BY Name ORDER BY ID")
    fun getInventoryListBySurveyId(inventoryTypeId: String,surveyId: String): List<InventoryTypeSelectionModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(insuranceRoomListi: List<InventoryTypeSelectionModel?>?)

    @Update
    fun update(insuranceRoomDataList: List<InventoryTypeSelectionModel?>?)

    @Query("Select * from inventory_type_selection WHERE inventoryTypeId = :inventoryTypeId AND InventoryItemId = :inventoryItemId")
    fun getInventoryItemById(inventoryTypeId: Int, inventoryItemId: Int): InventoryTypeSelectionModel


    @Query("Select * from inventory_type_selection WHERE  surveyId = :surveyId")
    fun getInventoryItemBySurveyId(surveyId: Int): List<InventoryTypeSelectionModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveyDetailsBySurveyId: InventoryTypeSelectionModel?)


    @Query("Select * from inventory_type_selection ORDER BY ID DESC LIMIT 5")
    fun getRecentInventoryList(): List<InventoryTypeSelectionModel>


    @Query("Select id from inventory_type_selection ORDER BY ID DESC LIMIT 1")
    fun getInventoryListLastId(): Long


    @Query("Select * from inventory_type_selection WHERE InventoryItemId = :inventoryitemid AND InventoryTypeId= :inventoryTypeId")
    fun getItemFromInventory(inventoryitemid: String,inventoryTypeId: String): InventoryTypeSelectionModel



    @Query("Select * from inventory_type_selection WHERE  inventoryItemId = :inventoryItemId")
    fun getCommonItemListByItemId(inventoryItemId: String): List<InventoryTypeSelectionModel>

    @Query("Select * from inventory_type_selection WHERE  Name = :name")
    fun checkInventoryItemIsThere(name: String): List<InventoryTypeSelectionModel>


    @Query("DELETE FROM inventory_type_selection WHERE Name = :inventoryItemId")
    fun deleteUserAddedItem(inventoryItemId: String)


}
