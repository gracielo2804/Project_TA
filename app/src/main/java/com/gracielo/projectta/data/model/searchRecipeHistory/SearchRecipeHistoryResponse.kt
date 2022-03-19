package com.gracielo.projectta.data.model.searchRecipeHistory


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchRecipeHistoryResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataHistory")
    var searchRecipeDataHistory: List<SearchRecipeDataHistory>,
    @SerializedName("message")
    var message: String
):Parcelable