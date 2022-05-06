package com.gracielo.projectta.data.model.admin.allMembershipTransaction


import com.google.gson.annotations.SerializedName

data class DataAllMembership(
    @SerializedName("id_paket")
    var idPaket: String,
    @SerializedName("id_trans")
    var idTrans: String,
    @SerializedName("id_user")
    var idUser: String,
    @SerializedName("nama_cust")
    var namaCust: String,
    @SerializedName("nama_paket")
    var namaPaket: String,
    @SerializedName("nominal")
    var nominal: String,
    @SerializedName("status_trans")
    var statusTrans: String,
    @SerializedName("tanggal")
    var tanggal: String
)