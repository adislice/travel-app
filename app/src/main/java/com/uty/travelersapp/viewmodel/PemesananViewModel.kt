package com.uty.travelersapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.TempatWisataArrayItem

class PemesananViewModel: ViewModel() {
    private val _tanggalPerjalanan = MutableLiveData<String>("")
    val tanggalPerjalanan: LiveData<String> = _tanggalPerjalanan

    private val _namaPemesan = MutableLiveData<String>("")
    val namaPemesan: LiveData<String> = _namaPemesan

    private val _noTelpPemesan = MutableLiveData<String>("")
    val noTelpPemesan: LiveData<String> = _noTelpPemesan

    private val _lokasiPenjemputan = MutableLiveData<LatLng>()
    val lokasiPenjemputan: LiveData<LatLng> = _lokasiPenjemputan

    private val _paketWisataTerpilih = MutableLiveData<PaketWisataItem>()
    val paketWisataTerpilih: LiveData<PaketWisataItem> = _paketWisataTerpilih

    private val _produkTerpilih = MutableLiveData<ProdukPaketWisata>()
    val produkTerpilih: LiveData<ProdukPaketWisata> = _produkTerpilih

    private val _tujuanWisata = MutableLiveData<ArrayList<TempatWisataArrayItem>>()
    val tujuanWisata: LiveData<ArrayList<TempatWisataArrayItem>> = _tujuanWisata

    fun setTanggalPerjalanan(value: String) {
        _tanggalPerjalanan.value = value
    }

    fun setNamaPemesan(value: String) {
        _namaPemesan.value = value
    }

    fun setNoTelpPemesan(value: String) {
        _noTelpPemesan.value = value
    }

    fun setLokasiPenjemputan(value: LatLng) {
        _lokasiPenjemputan.value = value
    }

    fun setPaketWisataTerpilih(value: PaketWisataItem) {
        _paketWisataTerpilih.value = value
    }

    fun setProdukTerpilih(value: ProdukPaketWisata) {
        _produkTerpilih.value = value
    }

    fun setTujuanWisata(value: ArrayList<TempatWisataArrayItem>) {
        _tujuanWisata.value = value
    }


}