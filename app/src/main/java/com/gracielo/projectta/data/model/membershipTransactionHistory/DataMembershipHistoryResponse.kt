package com.gracielo.projectta.data.model.membershipTransactionHistory


import com.google.gson.annotations.SerializedName

data class DataMembershipHistoryResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataMembershipHistory")
    var dataMembershipHistory: List<DataMembershipHistory>,
    @SerializedName("message")
    var message: String
)