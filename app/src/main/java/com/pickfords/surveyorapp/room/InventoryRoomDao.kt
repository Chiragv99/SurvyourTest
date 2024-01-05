package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.InventoryRoomModel

@Dao
interface InventoryRoomDao {

    @Query("Select * from inventory_room WHERE SurveyId = :surveyid")
    fun getInsuranceRoomList(surveyid: Int): List<InventoryRoomModel>

    @Query("Select * from inventory_room")
    fun getInsuranceRoom(): List<InventoryRoomModel>


    @Query("Select * from inventory_room where IsNew = :isNew")
    fun getNewlyAddedRoom(isNew: Boolean): List<InventoryRoomModel>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(insuranceRoomList: List<InventoryRoomModel?>?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(insuranceRoomList: InventoryRoomModel?)

    @Update
    fun update(insuranceRoomDataList: List<InventoryRoomModel?>?)

    @Query("UPDATE inventory_room SET Room = :roomName WHERE RoomId = :id")
    fun updateRoomName( id: Int ,roomName: String): Int


    @Query("Select id from inventory_room ORDER BY ID DESC LIMIT 1")
    fun getRoomLastId(): Int


    @Query("DELETE FROM inventory_room WHERE id = :roomId")
    fun delete(roomId: String)

    @Query("UPDATE inventory_room SET IsNew = :isNew WHERE id = :roomId")
    fun updateNewRoomName( isNew: Boolean ,roomId: String): Int

}