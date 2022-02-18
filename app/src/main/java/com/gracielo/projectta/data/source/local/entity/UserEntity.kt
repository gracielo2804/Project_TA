package com.gracielo.projectta.data.source.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val id: String,
    val name: String,
    val username: String,
    val email:String,
    val height: Int,
    val weight: Int,
    val gender: String,
    val kalori: Double,
    val age: Int
):Parcelable
