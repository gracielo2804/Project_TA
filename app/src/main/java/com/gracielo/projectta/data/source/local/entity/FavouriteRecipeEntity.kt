package com.gracielo.projectta.data.source.local.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favourite_recipe")
@Parcelize
data class FavouriteRecipeEntity(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id_recipe_favourite")
    val id_recipe_favourite: String,

    @NonNull
    @ColumnInfo(name="id_user")
    val id_user: String,

    @NonNull
    @ColumnInfo(name="id_recipe")
    val id_recipe: String,

//    @NonNull
//    @ColumnInfo(name="ingredient_list_info")
//    val ingredients_list_info: String,

//    @NonNull
//    @ColumnInfo(name="images_link")
//    val images_link: String,

): Parcelable