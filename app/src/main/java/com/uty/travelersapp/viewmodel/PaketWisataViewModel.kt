package com.uty.travelersapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.repository.PaketWisataRepository

class PaketWisataViewModel: ViewModel() {
    private val repository: PaketWisataRepository
    private val _allPaketWisata = MutableLiveData<List<PaketWisataItem>>()
    val allPaketWisata: LiveData<List<PaketWisataItem>> = _allPaketWisata
    private var _tujuanWisata = MutableLiveData<ArrayList<TempatWisataArrayItem>>()
    val tujuanWisata: LiveData<ArrayList<TempatWisataArrayItem>> = _tujuanWisata

    init {
        repository = PaketWisataRepository().getInstance()
        repository.getAllPaketWisata(_allPaketWisata)
    }

    fun getTujuanWisata(twList: List<TempatWisataArrayItem>) {
        repository.getAllTujuanWisataByModel(_tujuanWisata, twList)
    }

}