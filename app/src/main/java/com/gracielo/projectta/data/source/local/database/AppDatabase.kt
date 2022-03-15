package com.gracielo.projectta.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [UserEntity::class, Ingredients::class,ShoppingListEntity::class,UserNutrientsEntity::class],exportSchema = false,version=1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val passphrase: ByteArray = SQLiteDatabase.getBytes("Aplikasi Resep".toCharArray())
        val factory = SupportFactory(passphrase)
        @JvmStatic
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppRecipe.db"
                )
                    .allowMainThreadQueries()
//                    .openHelperFactory(factory)
                    .build()
                INSTANCE = instance
                instance
            }
    }
}