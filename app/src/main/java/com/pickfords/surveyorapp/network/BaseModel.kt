package com.pickfords.surveyorapp.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseModel<T> : Serializable {
    @SerializedName("Success")
    var success: Boolean = false
    @SerializedName("Message")
    var message: String = ""
    @SerializedName("Data")
    var data: T? = null
}
