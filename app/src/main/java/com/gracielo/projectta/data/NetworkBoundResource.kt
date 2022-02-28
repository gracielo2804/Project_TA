package com.gracielo.projectta.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.gracielo.projectta.data.source.remote.network.ApiResponses
import com.gracielo.projectta.util.AppExecutors
import com.gracielo.projectta.vo.Resource

abstract class NetworkBoundResource<ResultType, RequestType>(private val mExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    result.value = Resource.success(newData)
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): LiveData<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun createCall(): LiveData<ApiResponses<RequestType>>

    protected abstract fun saveCallResult(data: RequestType)

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            result.value = Resource.loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiResponses.Success ->
                    mExecutors.diskIO().execute {
                        saveCallResult(response.data)
                        mExecutors.mainThread().execute {
                            result.addSource(loadFromDB()) { newData ->
                                result.value = Resource.success(newData)
                            }
                        }
                    }
                is ApiResponses.Empty -> mExecutors.mainThread().execute {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.success(newData)
                    }
                }
                is ApiResponses.Error -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.error(response.errorMessage, newData)
                    }
                }
            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}