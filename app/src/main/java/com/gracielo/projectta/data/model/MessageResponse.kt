package com.gracielo.projectta.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("dataUser")
    val dataUser: DataUser?,
    @SerializedName("dataUserProfile")
    val dataUserProfile: DataUserProfile?
):Parcelable