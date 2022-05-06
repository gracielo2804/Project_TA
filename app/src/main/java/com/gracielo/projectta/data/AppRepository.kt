package com.gracielo.projectta.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.*
import com.gracielo.projectta.data.model.DataIngrdient
import com.gracielo.projectta.vo.Resource
import com.gracielo.projectta.data.model.IngredientListResponse
import com.gracielo.projectta.data.model.favouriteRecipe.DataFavRecipe
import com.gracielo.projectta.data.source.AppDataSource
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.IngredientsSearch
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

//        override fun getIngredients(): LiveData<Resource<PagingData<Ingredients>>> {
//            return object : NetworkBoundResource<PagingData<Ingredients>, List<DataIngrdient>>(appExecutors) {
//                public override fun loadFromDB(): LiveData<PagingData<Ingredients>> {
//                    return Pager(
//                        config = PagingConfig(
//                            enablePlaceholders = false,
//                            pageSize=20,
//                            initialLoadSize = 20),
//                        pagingSourceFactory = {
//                            localDataSource.getAllIngredients()
//                        }
//                    ).liveData
//
//                }
//                override fun shouldFetch(data: PagingData<Ingredients>?): Boolean {
//                   return data==null
//                }
//
//
//
//                public override fun createCall(): LiveData<ApiResponses<List<DataIngrdient>>> =
//                    remoteRepository.getAllIngredients()
//
//                public override fun saveCallResult(data: List<DataIngrdient>) {
//                    val movieList = ArrayList<Ingredients>()
//                    for (item in data) {
//                        val movie = Ingredients(
//                            item.id,
//                            item.name.lowercase(),
//                            item.image,
//                            false
//                        )
//                        movieList.add(movie)
//                    }
//                    localDataSource.insertIngredients(movieList)
//                }
//
//            }.asLiveData()
//        }
        override fun getIngredients(): LiveData<Resource<List<Ingredients>>> {
            return object : NetworkBoundResource<List<Ingredients>, List<DataIngrdient>>(appExecutors) {
                public override fun loadFromDB(): LiveData<List<Ingredients>> {
                   return localDataSource.getAllIngredients()
                }
                override fun shouldFetch(data: List<Ingredients>?): Boolean =
                    data == null|| data.isEmpty()


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
        override fun getSearchIngredients(search:String): LiveData<Resource<List<Ingredients>>> {
            return object : NetworkBoundResource<List<Ingredients>, List<DataIngrdient>>(appExecutors) {
                public override fun loadFromDB(): LiveData<List<Ingredients>> {
                    return localDataSource.getSearchIngredients(search)
                }
                override fun shouldFetch(data: List<Ingredients>?): Boolean =
                    data==null||data.isEmpty()


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

    override fun getFavRecipe(idUser: String): LiveData<Resource<List<FavouriteRecipeEntity>>> {
        return object : NetworkBoundResource<List<FavouriteRecipeEntity>, List<DataFavRecipe>>(appExecutors) {
            public override fun loadFromDB(): LiveData<List<FavouriteRecipeEntity>> {
                return localDataSource.getFavRecipe(idUser)
            }
            override fun shouldFetch(data: List<FavouriteRecipeEntity>?): Boolean =
                data==null||data.isEmpty()


            public override fun createCall(): LiveData<ApiResponses<List<DataFavRecipe>>> =
                remoteRepository.getFavoriteRecipeUser(idUser)

            public override fun saveCallResult(data: List<DataFavRecipe>) {
                val movieList = ArrayList<FavouriteRecipeEntity>()
                for (item in data) {
                    val movie = FavouriteRecipeEntity(
                        item.idFavRecipe,
                        item.idUsers,
                        item.idRecipe
                    )
                    movieList.add(movie)
                }
                localDataSource.insertFavRecipeList(movieList)
            }
        }.asLiveData()
    }

    override fun insertFavRecipe(favRecipe: FavouriteRecipeEntity) {
        localDataSource.insertFavRecipe(favRecipe)
    }

    override fun insertListFavRecipe(listFavRecipe: List<FavouriteRecipeEntity>) {
        localDataSource.insertFavRecipeList(listFavRecipe)
    }

    override fun deleteFavRecipe(favRecipe: FavouriteRecipeEntity) {
        localDataSource.deleteFavRecipe(favRecipe)
    }
//    override fun getIngredients(): LiveData<Resource<PagedList<Ingredients>>> {
//        return object : NetworkBoundResource<PagedList<Ingredients>, List<DataIngrdient>>(appExecutors) {
//            public override fun loadFromDB(): LiveData<PagedList<Ingredients>> {
//                val config = PagedList.Config.Builder().apply {
//                    setEnablePlaceholders(false)
//                    setInitialLoadSizeHint(20)
//                    setPageSize(20)
//                }.build()
//                return LivePagedListBuilder(localDataSource.getAllIngredients(), config).build()
//            }
//            override fun shouldFetch(data: PagedList<Ingredients>?): Boolean =
//                data == null|| data.isEmpty()
//
//
//            public override fun createCall(): LiveData<ApiResponses<List<DataIngrdient>>> =
//                remoteRepository.getAllIngredients()
//
//            public override fun saveCallResult(data: List<DataIngrdient>) {
//                val movieList = ArrayList<Ingredients>()
//                for (item in data) {
//                    val movie = Ingredients(
//                        item.id,
//                        item.name.lowercase(),
//                        item.image,
//                        false
//                    )
//                    movieList.add(movie)
//                }
//                localDataSource.insertIngredients(movieList)
//            }
//
//        }.asLiveData()
//    }
//    override fun getSearchIngredients(search:String): LiveData<Resource<PagedList<IngredientsSearch>>> {
//            return object : NetworkBoundResource<PagedList<IngredientsSearch>, List<DataIngrdient>>(appExecutors) {
//                public override fun loadFromDB(): LiveData<PagedList<IngredientsSearch>> {
//                    val config = PagedList.Config.Builder().apply {
//                        setEnablePlaceholders(false)
//                        setInitialLoadSizeHint(20)
//                        setPageSize(20)
//                    }.build()
//                    return LivePagedListBuilder(localDataSource.getSearchIngredients(search), config).build()
//                }
//                override fun shouldFetch(data: PagedList<IngredientsSearch>?): Boolean =
//                    false
//
//
//                public override fun createCall(): LiveData<ApiResponses<List<DataIngrdient>>> =
//                    remoteRepository.getAllIngredients()
//
//                public override fun saveCallResult(data: List<DataIngrdient>) {
//                    val movieList = ArrayList<Ingredients>()
//                    for (item in data) {
//                        val movie = Ingredients(
//                            item.id,
//                            item.name.lowercase(),
//                            item.image,
//                            false
//                        )
//                        movieList.add(movie)
//                    }
//                    localDataSource.insertIngredients(movieList)
//                }
//            }.asLiveData()
//        }
}