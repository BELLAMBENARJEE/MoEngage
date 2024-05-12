package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Constants
import com.example.myapplication.data.Articles
import com.example.myapplication.network.asyncGetHttpRequest

class MainViewModel : ViewModel() {

    private var _articles = MutableLiveData<List<Articles>>()
    val articles: LiveData<List<Articles>>
        get() = _articles
    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error
    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        fetchArticles()
    }

    /**fetching the articleslist*/
    private fun fetchArticles() {
        asyncGetHttpRequest(
            endpoint = Constants.BASE_URL,
            onSuccess = {
                _articles.postValue(it.response.articles)
                _error.postValue(false)
                _loading.postValue(false)
            },
            onError = {
                _error.postValue(true)
                _loading.postValue(false)
            }
        )
    }

}