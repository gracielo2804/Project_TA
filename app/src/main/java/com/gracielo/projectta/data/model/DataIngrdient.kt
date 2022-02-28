package com.gracielo.projectta.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataIngrdient(
    val id: Int ,
    val image: String,
    val name: String,
    val isSelected:Boolean

):Parcelable