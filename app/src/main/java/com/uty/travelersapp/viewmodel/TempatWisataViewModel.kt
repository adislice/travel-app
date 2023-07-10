package com.uty.travelersapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.repository.TempatWisataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class TempatWisataViewModel: ViewModel() {

    private val repository: TempatWisataRepository
    private val _allTempatWisata = MutableLiveData<List<TempatWisataItem>>()
    val allTempatWisata: LiveData<List<TempatWisataItem>> = _allTempatWisata
    private val _detailTempatWisata = MutableLiveData<TempatWisataItem>()
    val detailTempatWisata: LiveData<TempatWisataItem> = _detailTempatWisata
    private val _tempatWisataHome = MutableLiveData<List<TempatWisataItem>>()
    val tempatWisataHome: LiveData<List<TempatWisataItem>> = _tempatWisataHome

    private val searchQuery = MutableLiveData<String>("")

    init {
        repository = TempatWisataRepository().getInstance()
        repository.getAllTempatWisata(_allTempatWisata)
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAllTempatWisataRealtime().collect {response ->
                _allTempatWisata.postValue(response)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            repository.getTempatWisataHome().collect {response ->
                _tempatWisataHome.postValue(response)
            }
        }
    }

    fun getAllTempatWisataRealtime() = liveData(Dispatchers.IO) {
        repository.getAllTempatWisataRealtime().collect {response ->
            emit(response)
        }
    }

//    fun getAllTempatWisataSearch(searchQuery: String) = searchQuery.distinctUntilChanged()

//    var cobaAllTempatWisata = searchQuery.distinctUntilChanged().switchMap { search ->
//        Log.d("kencana", "search query changed")
//        return@switchMap liveData(Dispatchers.IO) {
//            repository.getAllTempatWisataSearch(0, search).collect { response ->
//                emit(response)
//            }
//        }
//    }

    fun performSearch(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAllTempatWisataRealtime(0, search).collect {response ->
                _allTempatWisata.postValue(response)
            }
        }
    }

    fun setSearchQuery(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun getAllTempatWisata() {
        repository.getAllTempatWisata(_allTempatWisata)
    }

    fun getDetailTempatWisata(id: String) {
        repository.getDetailTempatWisata(_detailTempatWisata, id)
    }



}