package com.gracielo.projectta.data.source.local.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user")
@Parcelize
data class UserEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    val id: String,

    @NonNull
    @ColumnInfo(name="name")
    val name: String,

    @NonNull
    @ColumnInfo(name="username")
    val username: String,

    @NonNull
    @ColumnInfo(name="email")
    val email:String,

    @NonNull
    @ColumnInfo(name="tipe")
    val tipe:String,

    @NonNull
    @ColumnInfo(name="expired")
    val expired:String,

    @NonNull
    @ColumnInfo(name="height")
    var height: Int,

    @NonNull
    @ColumnInfo(name="weight")
    var weight: Int,

    @NonNull
    @ColumnInfo(name="gender")
    val gender: String,

    @NonNull
    @ColumnInfo(name="kalori")
    var kalori: Double,

    @NonNull
    @ColumnInfo(name="age")
    var age: Int,

    @NonNull
    @ColumnInfo(name="activities")
    var activities: Int
):Parcelable
