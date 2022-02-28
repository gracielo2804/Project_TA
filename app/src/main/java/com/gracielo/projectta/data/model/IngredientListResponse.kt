package com.gracielo.projectta.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientListResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("dataIngredients")
    val dataIngrdients: List<DataIngrdient>,

    @SerializedName("message")
    val message: String
):Parcelable