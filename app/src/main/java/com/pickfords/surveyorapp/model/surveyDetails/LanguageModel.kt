package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "language_list")
class LanguageModel {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("LanguageId")
    lateinit var languageId: String

    @ColumnInfo(name = "Language")
    @SerializedName("Language")
    var language: String? = null

}