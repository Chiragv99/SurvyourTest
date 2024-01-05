package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel

@Dao
interface SequenceDetailsDao {

    @Query("Select * from sequence_details")
    fun getSurveyDetailsList(): List<SequenceDetailsModel>


    @Query("Select * from sequence_details where isChangedField = 1 or isDelete = 1 or surveyInventoryId LIKE 'ADD%'")
    fun getSyncSurveyDetailsList(): List<SequenceDetailsModel>

    @Query("Select * from sequence_details  WHERE surveyId = :surveyId AND  isDelete = 0 AND surveyInventoryId LIKE 'ADD%'")
    fun getSyncSurveyDetailsListBySurveyId(surveyId: String): List<SequenceDetailsModel>


    @Query("Select * from sequence_details WHERE surveyId = :surveyId AND isDelete = 0 AND SequenceId = :sequenceId")
    fun getSurveyDetailsBySurveyIdAndSequenceId(surveyId: String,sequenceId: String): List<SequenceDetailsModel>

    @Query("Select * from sequence_details WHERE surveyId = :surveyId AND isDelete = 0 AND SequenceId = :sequenceId AND SurveySequenceLeg Is NULL")
    fun getSurveyDetailsBySurveyIdAndSequenceIdByLeg(surveyId: String,sequenceId: String): List<SequenceDetailsModel>

    @Query("Select * from sequence_details WHERE surveyId = :surveyId AND sequenceId = :sequenceId AND   isDelete = 0  ORDER BY SurveySequenceLeg ASC, Room ASC,InventoryItemName ASC")
//    @Query("Select s.*, a.CityName as fromCity, c.CountryName as fromCountry, a2.CityName as toCity, c2.CountryName as toCountry from sequence_details as s INNER JOIN address_list as a on s.legFromAddressId = a.surveyAddressId INNER JOIN country_list as c on a.CountryId = c.countryId INNER JOIN address_list as a2 on s.legToAddressId = a2.surveyAddressId INNER JOIN country_list as c2 on a2.CountryId = c2.countryId WHERE s.SurveyId = :surveyId AND s.SequenceId = :sequenceId AND s.isDelete = 0")
    fun getSurveyDetailsBySurveyId(surveyId: String,sequenceId:String): List<SequenceDetailsModel>

    @Query("Select SUM(volume * count) FROM sequence_details WHERE surveyId = :surveyId  AND sequenceId = :sequenceId  AND IsRemain = :isRemaining AND isDelete = 0")
    fun getSumVolume(surveyId: String,sequenceId:String, isRemaining:String): Float?

    //FRSL = Floor, Room, Sequence
    @Query("Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.isDelete, sd.InventoryTypeId, sd.InventoryNameId, sd.IsCrate, sd.Volume, sd.Count, sd.IsCustomize, sd.InventoryValue, sd.SurveySequenceLeg, sd.IsNew from sequence_details AS sd WHERE Floor = :floor AND Room = :room AND Sequence = :sequence AND SurveyId = :surveyId AND SurveySequenceLeg IsNull  AND  isDelete = 0  AND  IsSubItem = 0 ORDER BY InventoryTypeId DESC")
    fun getSurveyDetailsBySurveyIdFRS(surveyId: String, floor: String, room: String, sequence: String): List<SequenceDetailsModel>


    //FRSL = Floor, Room, Sequence, Leg(optional)
    @Query("Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.isDelete, sd.InventoryTypeId, sd.InventoryNameId, sd.IsCrate, sd.Volume, sd.Count, sd.IsCustomize, sd.InventoryValue, sd.IsNew from sequence_details AS sd WHERE SurveySequenceLeg= :leg AND Floor = :floor AND Room = :room AND Sequence = :sequence AND SurveyId = :surveyId AND isDelete = 0 AND IsSubItem = 0 ORDER BY InventoryTypeId DESC")
    fun getSurveyDetailsBySurveyIdFRSL(surveyId: String, floor: String, room: String, sequence: String, leg: String = ""): List<SequenceDetailsModel>


    //FRSL = Floor, Room, Sequence
    @Query("Select * from sequence_details WHERE Floor = :floor AND Room = :room AND SequenceId = :sequenceId AND SurveyId = :surveyId AND IsCustomize = 1 AND isDelete = 0  ORDER BY InventoryTypeId DESC")
    fun getSurveyDetailsBySurveyIdFRSCustom(surveyId: String, floor: String, room: String, sequenceId: String): List<SequenceDetailsModel>

    //FRSL = Floor, Room, Sequence, Leg(optional)
    @Query("Select * from sequence_details WHERE SurveySequenceLeg= :leg AND Floor = :floor AND Room = :room AND Sequence = :sequence AND SurveyId = :surveyId AND IsCustomize = 1 AND isDelete = 0 ORDER BY InventoryTypeId DESC")
    fun getSurveyDetailsBySurveyIdFRSLCustom(surveyId: String, floor: String, room: String, sequence: String, leg: String = ""): List<SequenceDetailsModel>


    //FRSL = Floor, Room, Sequence, inventoryTypeId, ItemName, Leg(optional)
    /* TODO Jaimit write below query to get selected Item from sequens detail
select * from sequence_details where  Sequence = "PF21092754-91" and SurveyId = "327" and
            Floor = "A/3" and Room = "MASTER" and InventoryTypeId = 1 and item = "atul2"*/
    //    AND  SurveySequenceLeg= :selectedLeg  // TODO jaimit
    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom " +
                "AND InventoryTypeId = :inventoryTypeId AND item = :itemName AND isDelete = 0 ORDER BY surveyInventoryId DESC")
    fun getSurveyDetailsBySurveyIdFRSItem(selectedSequence: String,
                                          surveyId: String,
                                          selectedFloor: String,
                                          selectedRoom: String,
                                          inventoryTypeId: String,
                                          itemName: String): SequenceDetailsModel

    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom AND SurveySequenceLeg = :selectedLeg " +
                "AND InventoryTypeId = :inventoryTypeId AND item = :itemName AND isDelete = 0 ORDER BY surveyInventoryId DESC")
    fun getSurveyDetailsBySurveyIdFRSLItem(selectedSequence: String,
                                           surveyId: String,
                                           selectedFloor: String,
                                           selectedRoom: String,
                                           inventoryTypeId: String,
                                           itemName: String,
                                           selectedLeg: String): SequenceDetailsModel

    @Query(
        "Select * from sequence_details WHERE surveyInventoryId = :surveyInventoryId AND isDelete = 0 ORDER BY surveyInventoryId DESC LIMIT 1")
    fun getSurveyDetailByInventoryItem(surveyInventoryId: String): SequenceDetailsModel

    //FRSL = Floor, Room, Sequence, Leg(optional), I=InventoryItemId, I=InventoryTypeId
    //    AND  SurveySequenceLeg= :selectedLeg // TODO jaimit
    /* select * from sequence_details where  Sequence = "PF00084-91" and SurveyId = "331" and Floor = "A/1" and Room = "BASEMENT" and InventoryTypeId = 1 and InventoryNameId = 4 and SurveySequenceLeg IS NULL OR SurveySequenceLeg=""  and item ="E Carton (Books)" */
    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom " +
                "AND item = :itemName  AND isDelete = 0 ORDER BY surveyInventoryId DESC LIMIT 1")
    fun getSurveyDetailsBySurveyIdFRSIIItem(selectedSequence: String,
                                            surveyId: String,
                                            selectedFloor: String,
                                            selectedRoom: String,
                                            itemName: String): SequenceDetailsModel

    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom " +
                "AND InventoryTypeId = :inventoryTypeId AND item = :itemName AND isDelete = 0 ORDER BY surveyInventoryId DESC LIMIT 1")
    fun getSurveyDetailsBySurveyIdFRSIIItems(selectedSequence: String,
                                             surveyId: String,
                                             selectedFloor: String,
                                             selectedRoom: String,
                                             inventoryTypeId: String,
                                             itemName: String): SequenceDetailsModel

    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom AND SurveySequenceLeg = :selectedLeg " +
                "AND InventoryTypeId = :inventoryTypeId AND item = :itemName AND InventoryNameId= :inventoryNameId AND isDelete = 0 ORDER BY surveyInventoryId DESC LIMIT 1")
    fun getSurveyDetailsBySurveyIdFRSLIIItem(selectedSequence: String,
                                             surveyId: String,
                                             selectedFloor: String,
                                             selectedRoom: String,
                                             inventoryTypeId: String,
                                             itemName: String,
                                             selectedLeg: String,
                                             inventoryNameId: String): SequenceDetailsModel


    @Query(
        "Select sd.surveyInventoryId, sd.surveySequenceId, sd.SurveyId, sd.Item, sd.IsDismantle, sd.IsCard, sd.IsBubbleWrap, sd.IsRemain, sd.IsFullExportWrap, sd.isDelete, sd.IsCrate, sd.IsCustomize, sd.IsNew from sequence_details AS sd " +
                "WHERE Sequence = :selectedSequence AND SurveyId = :surveyId AND Floor = :selectedFloor AND Room = :selectedRoom AND SurveySequenceLeg = :selectedLeg " +
                "AND InventoryTypeId = :inventoryTypeId AND item = :itemName AND isDelete = 0 ORDER BY surveyInventoryId DESC LIMIT 1")
    fun getSurveyDetailsBySurveyIdFRSLIIItem(selectedSequence: String,
                                             surveyId: String,
                                             selectedFloor: String,
                                             selectedRoom: String,
                                             inventoryTypeId: String,
                                             itemName: String,
                                             selectedLeg: String): SequenceDetailsModel


    @Query(
        "Select sd.surveyInventoryId from sequence_details AS sd " +
                "WHERE SurveyId = :surveyId AND SequenceId = :sequenceId AND  SurveySequenceLeg = :selectedLeg")
    fun getLegUseInInventory(
        surveyId: String,
        sequenceId: String,
        selectedLeg: String): String

    @Query("Select sd.surveyInventoryId from sequence_details AS sd " + "WHERE SurveyId = :surveyId AND SequenceId = :sequenceId AND   isDelete = 0")
    fun getSequenceUseInInventory(surveyId: String, sequenceId: String): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(surveyDetailsBySurveyId: List<SequenceDetailsModel?>?)

    @Insert
    fun insertAllCopy(surveyDetailsBySurveyId: List<SequenceDetailsModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveyDetailsBySurveyId: SequenceDetailsModel?)

    @Update
    fun update(surveyDetailsBySurveyIdData: List<SequenceDetailsModel?>?)

    @Update
    fun update(surveyDetailsBySurveyIdData: SequenceDetailsModel?)

//    @Query("UPDATE sequence_details SET isSubItem = :surveySequenceId WHERE inventoryNameId = :inventoryNameId")
//    fun updateData(isSubItem:Boolean,)


    @Query("UPDATE sequence_details SET isDelete = :isDelete WHERE surveyInventoryId = :surveyInventoryId")
    fun updateIsDelete(surveyInventoryId: String, isDelete: Boolean = false)

    @Query("UPDATE sequence_details SET isChangedField = :isChanged WHERE surveyInventoryId = :surveyInventoryId")
    fun updateIsChangedField(surveyInventoryId: String, isChanged: Boolean = false)

    @Query("DELETE FROM sequence_details WHERE surveyInventoryId = :surveyInventoryId")
    fun delete(surveyInventoryId: String)

    @Query("DELETE FROM sequence_details WHERE surveyId = :surveyId")
    fun deleteBySurveyID(surveyId: String)

    @Query("DELETE FROM sequence_details")
    fun nukeTable()

    @Query("Select * from sequence_details WHERE item = :item AND surveySequence = :surveySequence AND ItemImage IS NOT NULL")
    fun getInventoryItemImage(item: String,surveySequence:String): SequenceDetailsModel

    @Query("UPDATE sequence_details SET SequenceFromAddressId = :fromAddId, SequenceToAddressId = :toAddId WHERE SurveyId = :surveyId AND Sequence = :sequence")
    fun updateSequenceAddress(surveyId: String, sequence: String, fromAddId: String, toAddId: String)



    @Query("select * from sequence_details where   SequenceId = :sequenceId    AND  SurveyId = :surveyId AND  InventoryItemName  LIKE  :InventoryTypeId")
    fun getCartoonItems(InventoryTypeId: String, surveyId: String, sequenceId: String): List<SequenceDetailsModel>


    @Query("Select DISTINCT SequenceId  from sequence_details where SurveyId = :surveyId  AND   isDelete = 0")
    fun getAllSequenceIdList(surveyId: String): List<String>


    // For Get SubCartoonItemList
    @Query("select * from sequence_details where InventoryNameId = :surveyNameId AND Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND isDelete = 0  AND  SurveySequenceLeg IS NULL ORDER BY isSubItem = 0 DESC")
    fun getAllCartoonSubItem(surveyNameId: String,sequence: String, room: String, floor: String,surveyId: String):  List<SequenceDetailsModel>

    @Query("select * from sequence_details where  Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND  InventoryItemName = :inventoryItemName AND isDelete = 0 AND  SurveySequenceLeg IS NULL ORDER BY isSubItem = 0 DESC")
    fun getAllCartoonSubItemForCustomItem(sequence: String, room: String, floor: String,surveyId: String,inventoryItemName: String):  List<SequenceDetailsModel>


    // For Get SubCartoonItemList By Leg
    @Query("select * from sequence_details where InventoryNameId = :surveyNameId AND Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND isDelete = 0 AND SurveySequenceLeg = :leg   ORDER BY isSubItem = 0 DESC")
    fun getAllCartoonSubItemByLeg(surveyNameId: String,sequence: String, room: String, floor: String,surveyId: String,leg: String):  List<SequenceDetailsModel>


    // For Get SubCartoonItemList
    @Query("select * from sequence_details where InventoryNameId = :surveyNameId AND Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND isDelete = 0  AND   SurveySequenceLeg IS NULL AND isSubItem = 1")
    fun getAllCartoonSubItemCount(surveyNameId: String,sequence: String, room: String, floor: String,surveyId: String):  List<SequenceDetailsModel>



    // For Get SubCartoonItemList By Leg
    @Query("select * from sequence_details where InventoryNameId = :surveyNameId AND Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND isDelete = 0 AND   SurveySequenceLeg = :leg  AND isSubItem = 1")
    fun getAllCartoonSubItemCountByLeg(surveyNameId: String,sequence: String, room: String, floor: String,surveyId: String,leg: String):  List<SequenceDetailsModel>




    @Query("select * from sequence_details where  Sequence = :sequence  AND Room = :room AND Floor = :floor AND SurveyId = :surveyId AND  InventoryItemName = :inventoryItemName AND isDelete = 0 AND isSubItem = 1 AND  SurveySequenceLeg IS NULL ORDER BY isSubItem = 0 DESC")
    fun getAllCartoonSubItemForCustomCountItem(sequence: String, room: String, floor: String,surveyId: String,inventoryItemName: String):  List<SequenceDetailsModel>


    // For Get Single SubCartoonItem
    @Query("select * from sequence_details where SurveyInventoryId = :surveyInventoryId")
    fun getSelectedCartoonSubItem(surveyInventoryId: String):  SequenceDetailsModel






    @Query("Select sd.IsUserAdded from sequence_details AS sd " + "WHERE SurveyInventoryId = :surveyInventoryId")
    fun getUserAddedOrNot(surveyInventoryId: String): Boolean

}