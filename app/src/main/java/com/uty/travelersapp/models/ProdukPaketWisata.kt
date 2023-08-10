package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId

data class ProdukPaketWisata (
    var id: String? = "",
    val harga: Double? = 0.0,
    val is_aktif: Boolean? = false,
    val jenis_kendaraan_id: String? = null,
    var jenis_kendaraan: JenisKendaraan? = null,
)