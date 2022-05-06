package com.gracielo.projectta.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataUser(
    val id: String,
    val name: String,
    val username: String,
    val email:String,
    val emailVerified:String,
    val tipe:String,
    val expired:String
):Parcelable
