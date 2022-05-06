package com.gracielo.projectta.data.model.membershipTransactionHistory


import com.google.gson.annotations.SerializedName

data class DataMembershipHistory(
    @SerializedName("expired_time")
    var expiredTime: String,
    @SerializedName("id_transaksi")
    var idTransaksi: String,
    @SerializedName("id_user")
    var idUser: String,
    @SerializedName("nama_paket")
    var namaPaket: String,
    @SerializedName("nominal")
    var nominal: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("tanggal")
    var tanggal: String
)