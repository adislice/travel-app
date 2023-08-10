package com.uty.travelersapp.models

import java.util.Date

data class Promo(
    var id: String? = null,
    val kode: String? = null,
    val nama: String? = null,
    val min_pembelian: Double? = 0.0,
    val max_potongan: Double? = 0.0,
    val persen: Int? = 0,
    val tanggal_mulai: Date? = null,
    val tanggal_akhir: Date? = null,
    val created_at: Date? = null
)
