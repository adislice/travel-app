package com.uty.travelersapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.uty.travelersapp.models.Transaksi
import com.uty.travelersapp.repository.TransaksiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class TransaksiViewModel: ViewModel() {
    private val repository: TransaksiRepository

    init {
        repository = TransaksiRepository().getInstance()
    }

    fun insertTransaksi(userId: String, dataTransaksi: Transaksi) = liveData(Dispatchers.IO) {
        repository.insertTransaksi(userId, dataTransaksi).collect { response ->
            emit(response)
        }
    }

    fun getUserTransaksi(userId: String) = liveData(Dispatchers.IO) {
        repository.getUserTransaksi(userId).collect {response ->
            emit(response)
        }
    }

    fun getDetailTransaksi(idTransaksi: String) = liveData(Dispatchers.IO) {
        repository.getDetailTransaksiRealtime(idTransaksi).collect { response ->
            emit(response)
        }
    }


}