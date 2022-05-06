package com.gracielo.projectta.ui.membership

data class TransactionMembership(
    val id_transaction:String,
    val id_users: String,
    val tanggal : String,
    val nominal : Int,
    val id_paket: Int,
    val status: Int,
    val expired : String,
)
