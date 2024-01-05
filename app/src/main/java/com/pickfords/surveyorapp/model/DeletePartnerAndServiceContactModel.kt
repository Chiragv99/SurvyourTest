package com.pickfords.surveyorapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "deletePartnerAndService")
class DeletePartnerAndServiceContactModel {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("PartnerId") var partnerId: Int = 0


    @ColumnInfo(name = "isDelete")
    @SerializedName("IsDelete") var IsDelete: Boolean? = false
}