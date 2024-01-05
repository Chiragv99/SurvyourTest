package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.surveyDetails.CityCountryModel

@Dao
interface AddressListDao {
    @Query("Select * from address_list")
    fun getAddressList(): List<AddressListModel>


    @Query("Select * from address_list  WHERE surveyId = :surveyId")
    fun getAddressListBySurvey(surveyId: String): List<AddressListModel>

    @Query("Select * from address_list WHERE surveyId = :surveyId AND isDelete = :isDelete")
    fun getAddressListBySurveyId(surveyId: String, isDelete: Boolean = false): List<AddressListModel>

    @Query("Select * from address_list WHERE surveyAddressId = :surveyAddressId")
    fun getAddressListByAddressId(surveyAddressId: String): AddressListModel

    @Query("Select a.surveyAddressId, a.CityName, c.CountryName from address_list as a inner join country_list as c on a.CountryId = c.countryId WHERE surveyAddressId = :surveyAddressId")
    fun getAddressByAddressId(surveyAddressId: String): CityCountryModel


    @Query("Select sd.TitleName from address_list AS sd WHERE surveyAddressId = :surveyAddressId")
    fun getAddressTittle(surveyAddressId: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(addressList: List<AddressListModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(addressList: AddressListModel?)

    @Update
    fun update(addressDataList: AddressListModel?)

    @Query("DELETE FROM address_list WHERE surveyAddressId = :surveyAddressId")
    fun delete(surveyAddressId: String)

    @Query("DELETE FROM address_list WHERE surveyId = :surveyId")
    fun deleteWithSurveyID(surveyId: String)
}