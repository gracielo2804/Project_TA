package com.gracielo.projectta.data.model.admin.allMembershipTransaction


import com.google.gson.annotations.SerializedName

data class AllMembershipResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataAllMembership")
    var dataAllMembership: List<DataAllMembership>,
    @SerializedName("message")
    var message: String
)