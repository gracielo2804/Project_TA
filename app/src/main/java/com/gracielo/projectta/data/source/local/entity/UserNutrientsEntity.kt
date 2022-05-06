package com.gracielo.projectta.data.source.local.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "userNutrients")
@Parcelize
data class UserNutrientsEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    val id: String,

    @NonNull
    @ColumnInfo(name="max_kalori")
    var maxKalori : Double,

    @NonNull
    @ColumnInfo(name="kalori_consumed")
    var kalori_consumed : Double,

    @NonNull
    @ColumnInfo(name="max_karbo")
    var maxKarbo : Double,

    @NonNull
    @ColumnInfo(name="karbo_consumed")
    var karbo_consumed : Double,

    @NonNull
    @ColumnInfo(name="max_gula")
    var maxGula : Double,

    @NonNull
    @ColumnInfo(name="gula_consumed")
    var gula_consumed : Double,

    @NonNull
    @ColumnInfo(name="max_lemak")
    var maxLemak : Double,

    @NonNull
    @ColumnInfo(name="lemak_consumed")
    var lemak_consumed : Double,

    @NonNull
    @ColumnInfo(name="max_protein")
    var maxProtein : Double,

    @NonNull
    @ColumnInfo(name="protein_consumed")
    var protein_consumed : Double,

    @ColumnInfo(name = "date")
    var tanggal : String,
): Parcelable