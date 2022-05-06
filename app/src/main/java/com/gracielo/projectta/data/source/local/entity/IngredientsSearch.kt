package com.gracielo.projectta.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "ingredients")
data class IngredientsSearch(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id:Int,

    @ColumnInfo(name = "name")
    val name:String,

    @ColumnInfo(name = "image")
    val image:String,

    @ColumnInfo(name = "isSelected")
    var isSelected : Boolean
)
