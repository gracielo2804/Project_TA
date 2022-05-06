package com.gracielo.projectta.data.source
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import androidx.room.withTransaction
//import com.bumptech.glide.load.HttpException
//import com.gracielo.projectta.data.source.local.database.AppDatabase
//import com.gracielo.projectta.data.source.local.entity.Ingredients
//import com.gracielo.projectta.data.source.remote.network.ApiServices
//import java.io.IOException
//import java.util.ArrayList
//
//@OptIn(ExperimentalPagingApi::class)
//class ExampleRemoteMediator(
//    private val query: String,
//    private val database: AppDatabase,
//    private val networkService :ApiServices
//) : RemoteMediator<Int, Ingredients>() {
//    val dbDao = database.appDao()
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, Ingredients>
//    ): MediatorResult {
//        return try {
//            // The network load method takes an optional after=<user.id>
//            // parameter. For every page after the first, pass the last user
//            // ID to let it continue from where it left off. For REFRESH,
//            // pass null to load the first page.
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> null
//                // In this example, you never need to prepend, since REFRESH
//                // will always load the first page in the list. Immediately
//                // return, reporting end of pagination.
//                LoadType.PREPEND ->
//                    return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val lastItem = state.lastItemOrNull()
//
//                    // You must explicitly check if the last item is null when
//                    // appending, since passing null to networkService is only
//                    // valid for initial load. If lastItem is null it means no
//                    // items were loaded after the initial REFRESH and there are
//                    // no more items to load.
//                    if (lastItem == null) {
//                        return MediatorResult.Success(
//                            endOfPaginationReached = true
//                        )
//                    }
//
//                    lastItem.id
//                }
//            }
//
//            // Suspending network load via Retrofit. This doesn't need to be
//            // wrapped in a withContext(Dispatcher.IO) { ... } block since
//            // Retrofit's Coroutine CallAdapter dispatches on a worker
//            // thread.
//            val response = networkService.getAllIngredients()
//
//            database.withTransaction {
//                if (loadType == LoadType.REFRESH) {
//                    postDao.deleteBySubreddit(subredditName)
//                    remoteKeyDao.deleteBySubreddit(subredditName)
//                }
//
//                remoteKeyDao.insert(SubredditRemoteKey(subredditName, data.after))
//                postDao.insertAll(items)
//            }
//
//            MediatorResult.Success(
//                endOfPaginationReached = response.nextKey == null
//            )
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//    }
//}