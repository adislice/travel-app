package com.uty.travelersapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.uty.travelersapp.models.Pemesanan
import com.uty.travelersapp.models.PemesananInsert
import com.uty.travelersapp.repository.PemesananRepository
import kotlinx.coroutines.Dispatchers

class PemesananViewModel: ViewModel() {
    private val repository: PemesananRepository

    init {
        repository = PemesananRepository().getInstance()
    }

    fun insertPemesanan(userId: String, dataPemesanan: PemesananInsert) = liveData(Dispatchers.IO) {
        repository.insertPemesanan(userId, dataPemesanan).collect { response ->
            emit(response)
        }
    }

    fun getUserPemesanan(userId: String) = liveData(Dispatchers.IO) {
        repository.getUserPemesanan(userId).collect {response ->
            emit(response)
        }
    }

    fun getDetailPemesanan(idPemesanan: String) = liveData(Dispatchers.IO) {
        repository.getDetailPemesananRealtime(idPemesanan).collect { response ->
            emit(response)
        }
    }


}