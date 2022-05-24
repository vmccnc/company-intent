package com.app.intent.vm

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.intent.api.RetrofitApi
import com.app.intent.api.model.RepoResponse
import com.app.intent.api.model.Repo
import com.app.intent.ui.screens.SearchWidgetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    val userDmesLive = MutableLiveData<List<Repo>>()

    val isLoading = MutableLiveData<Boolean>(false)
    val isError = MutableLiveData<Boolean>(false)


    val errorHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is Exception) {
            Log.d("gg", "dm:: error = ${throwable.message}")
            isLoading.value = false
            isError.value = true
        }
    }

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }


    init {
        Log.d("gg", "dm:: UserDm ViewModes coroutines")
    }

    fun loadUsers(q: String = "") {
        isLoading.value = true
        Log.d("gg", "dm:: isLoading 1 = ${isLoading.value}")
        viewModelScope.launch(errorHandler) {

            var listDeferred = RetrofitApi.retrofitApiService.getRepos(q)
            var resp: Response<RepoResponse> = listDeferred.await()

            if (resp.isSuccessful) {
                Log.d("gg", "dm:: success code = ${resp.code()}")
                val listResult = resp.body()?.items
                userDmesLive.value = listResult
                Log.d("gg", "dm:: list size ${listResult?.size}")
            } else {
                isLoading.value = false
                isError.value = true
                Log.d("gg", "dm:: Error message = ${resp.raw()}")
                Log.d("gg", "dm:: Error message = ${resp.errorBody()}")
            }
            isLoading.value = false
            Log.d("gg", "dm:: isLoading 2 = ${isLoading.value}")
        }
    }

}