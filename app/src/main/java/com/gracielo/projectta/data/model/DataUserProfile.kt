package com.gracielo.projectta.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataUserProfile(
    val id_users: String,
    val height: Int,
    val weight: Int,
    val gender: String,
    val kalori: Double,
    val age: Int
):Parcelable
