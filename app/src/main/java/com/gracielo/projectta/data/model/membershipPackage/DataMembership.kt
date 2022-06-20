package com.gracielo.projectta.data.model.membershipPackage


import com.google.gson.annotations.SerializedName

data class DataMembership(
    @SerializedName("id_package")
    var idPackage: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("price")
    var price: Int,
    @SerializedName("description")
    var description: String,
    @SerializedName("updated_at")
    var updatedAt: String
)