package com.gracielo.projectta.data.model.membershipPackage


import com.google.gson.annotations.SerializedName

data class membershipPackageResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataPackage")
    var dataMembership: List<DataMembership>,
    @SerializedName("message")
    var message: String
)