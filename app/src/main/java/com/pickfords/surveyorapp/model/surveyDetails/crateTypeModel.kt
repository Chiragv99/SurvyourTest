package com.pickfords.surveyorapp.model.surveyDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Entity(tableName = "crate_type")
class crateTypeModel {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "CrateTypeId")
    @SerializedName("CrateTypeId")
    @Expose
    private var crateTypeId: Int? = null

    @ColumnInfo(name = "CrateType")
    @SerializedName("CrateType")
    @Expose
    private var crateType: String? = null

    fun getCrateTypeId(): Int? {
        return crateTypeId
    }

    fun setCrateTypeId(crateTypeId: Int?) {
        this.crateTypeId = crateTypeId
    }

    fun getCrateType(): String? {
        return crateType
    }

    fun setCrateType(crateType: String?) {
        this.crateType = crateType
    }

}