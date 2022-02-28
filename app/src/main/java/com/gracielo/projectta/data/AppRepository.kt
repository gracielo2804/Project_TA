package com.gracielo.projectta.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gracielo.projectta.data.model.DataIngrdient
import com.gracielo.projectta.vo.Resource
import com.gracielo.projectta.data.model.IngredientListResponse
import com.gracielo.projectta.data.source.AppDataSource
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.remote.network.ApiResponses
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.util.AppExecutors
import java.util.ArrayList

open class AppRepository  constructor(remoteRepository: ApiServices,
                      val localDataSource: LocalDataSource,
                      val appExecutors: AppExecutors) :AppDataSource{
    private val remoteRepository = remoteRepository

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null
        fun getInstance(remoteRepository: ApiServices, localDataSource: LocalDataSource, appExecutors: AppExecutors): AppRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: AppRepository(remoteRepository, localDataSource, appExecutors).apply {
                        INSTANCE = this
                    }
            }
    }

        override fun getIngredients(): LiveData<Resource<PagedList<Ingredients>>> {
            return object : NetworkBoundResource<PagedList<Ingredients>, List<DataIngrdient>>(appExecutors) {
                public override fun loadFromDB(): LiveData<PagedList<Ingredients>> {
                    val config = PagedList.Config.Builder().apply {
                        setEnablePlaceholders(false)
                        setInitialLoadSizeHint(20)
                        setPageSize(20)
                    }.build()
                    return LivePagedListBuilder(localDataSource.getAllIngredients(), config).build()
                }


                override fun shouldFetch(data: PagedList<Ingredients>?): Boolean =
                    data == null || data.isEmpty()


                public override fun createCall(): LiveData<ApiResponses<List<DataIngrdient>>> =
                    remoteRepository.getAllIngredients()

                public override fun saveCallResult(data: List<DataIngrdient>) {
                    val movieList = ArrayList<Ingredients>()
                    for (item in data) {
                        val movie = Ingredients(
                            item.id,
                            item.name.lowercase(),
                            item.image,
                            false
                        )
                        movieList.add(movie)
                    }
                    localDataSource.insertIngredients(movieList)
                }

            }.asLiveData()
        }
}