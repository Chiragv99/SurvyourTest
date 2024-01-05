package com.pickfords.surveyorapp.model.surveyDetails

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventory_room")
class InventoryRoomModel {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id = 0

    @ColumnInfo(name = "RoomId")
    @SerializedName("RoomId")
    var roomId: Int = 0

    @ColumnInfo(name = "Room")
    @SerializedName("Room")
    var room: String? = null

    @ColumnInfo(name = "IsActive")
    @SerializedName("IsActive")
    var isActive: Boolean? = null

    @ColumnInfo(name = "CreatedBy")
    @SerializedName("CreatedBy")
    var createdBy: Int? = null

    @ColumnInfo(name = "SurveyId")
    @SerializedName("SurveyId")
    var surveyId: Int? = null

    @ColumnInfo(name = "UserRoomId")
    @SerializedName("UserRoomId")
    var userRoomId: Int = 0

    @ColumnInfo(name = "IsNew")
    @SerializedName("IsNew")
    var newRecord: Boolean? = false


}