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
    @ColumnInfo(name="height")
    val height: Int,

    @NonNull
    @ColumnInfo(name="weight")
    val weight: Int,

    @NonNull
    @ColumnInfo(name="gender")
    val gender: String,

    @NonNull
    @ColumnInfo(name="kalori")
    val kalori: Double,

    @NonNull
    @ColumnInfo(name="age")
    val age: Int
):Parcelable
