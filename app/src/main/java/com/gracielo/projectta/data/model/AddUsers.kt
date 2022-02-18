package com.gracielo.projectta.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddUsers(

    @SerializedName("function")
    val function: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("nama")
    val nama: String?,
):Parcelable
