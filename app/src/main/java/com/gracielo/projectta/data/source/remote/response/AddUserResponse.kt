package com.gracielo.projectta.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddUserResponse(

    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("message")
    val message: String
): Parcelable