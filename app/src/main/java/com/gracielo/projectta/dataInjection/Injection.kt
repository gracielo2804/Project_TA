package com.gracielo.projectta.dataInjection

import android.content.Context
import com.gracielo.projectta.data.AppRepository
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDatabase
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.util.AppExecutors

object Injection {
    fun provideRepository(context: Context): AppRepository {

        val database = AppDatabase.getInstance(context)

        val remoteDataSource = ApiServices.getInstance()
        val localDataSource = LocalDataSource.getInstance(database.appDao())
        val appExecutors = AppExecutors()
        return AppRepository.getInstance(remoteDataSource!!, localDataSource, appExecutors)
    }
}