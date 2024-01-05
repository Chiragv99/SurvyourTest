package com.pickfords.surveyorapp.model.address

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "country_list")
class CountryListModel {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("CountryId")
    lateinit var countryId: String

    @ColumnInfo(name = "CountryName")
    @SerializedName("CountryName")
    var countryName: String? = null
}