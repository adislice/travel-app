package com.uty.travelersapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.repository.PaketWisataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PaketWisataViewModel: ViewModel() {
    private val repository: PaketWisataRepository
    private val _allPaketWisata = MutableLiveData<List<PaketWisataItem>>()
    val allPaketWisata: LiveData<List<PaketWisataItem>> = _allPaketWisata
    private var _tujuanWisata = MutableLiveData<ArrayList<TempatWisataArrayItem>>()
    val tujuanWisata: LiveData<ArrayList<TempatWisataArrayItem>> = _tujuanWisata
    private var _homePaketWisata = MutableLiveData<ArrayList<PaketWisataItem>>()
    var homePaketWisata: LiveData<ArrayList<PaketWisataItem>> = _homePaketWisata

    private var paketWisataId = MutableLiveData<String>()

    init {
        repository = PaketWisataRepository().getInstance()
        repository.getAllPaketWisata(_allPaketWisata)
        viewModelScope.launch {
            repository.getAllPaketWisataRealtime(5).collect { response ->
                _homePaketWisata.value = response
            }
        }

    }

    fun getTujuanWisata(twList: List<String>) {
        repository.getAllTujuanWisataByModel(_tujuanWisata, twList)
    }


    var getProdukPaketWisata = paketWisataId.distinctUntilChanged().switchMap { paketId ->
        liveData(Dispatchers.IO) {
            Log.d("kencana", "fetching data produk paket: " + this@PaketWisataViewModel.paketWisataId.value)
            repository.getProdukPaketWisata(paketId).collect { response ->
                emit(response)
            }
        }
    }

    fun setPaketWisataId(idPaket: String) {
        Log.d("kencana", "set paketId old:" + this.paketWisataId.value)
        this.paketWisataId.value = idPaket
        Log.d("kencana", "set paketId new:" + this.paketWisataId.value)
    }

    var getDetailPaketWisata = paketWisataId.distinctUntilChanged().switchMap { paketId ->
        liveData(Dispatchers.IO) {
            repository.getDetailPaketWisata(paketId).collect { response ->
                emit(response)
            }
        }
    }

    fun getAllPaketWisataRealtime(limit: Int = 0) = liveData(Dispatchers.IO) {
        repository.getAllPaketWisataRealtime(limit).collect { response ->
            emit(response)
        }
    }


}