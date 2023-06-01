package com.uty.travelersapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.repository.TempatWisataRepository

class TempatWisataViewModel: ViewModel() {

    private val repository: TempatWisataRepository
    private val _allTempatWisata = MutableLiveData<List<TempatWisataItem>>()
    val allTempatWisata: LiveData<List<TempatWisataItem>> = _allTempatWisata
    private val _detailTempatWisata = MutableLiveData<TempatWisataItem>()
    val detailTempatWisata: LiveData<TempatWisataItem> = _detailTempatWisata

    init {
        repository = TempatWisataRepository().getInstance()
        repository.getAllTempatWisata(_allTempatWisata)
    }

    fun getAllTempatWisata() {
        repository.getAllTempatWisata(_allTempatWisata)
    }

    fun getDetailTempatWisata(id: String) {
        repository.getDetailTempatWisata(_detailTempatWisata, id)
    }

}