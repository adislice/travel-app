package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class PaketWisataItem (
    var id: String? = "",
    val nama: String? = null,
    val deskripsi: String? = null,
    val fasilitas: String? = null,
    val jam_keberangkatan: String? = null,
    val foto: ArrayList<String>? = null,
    var tempat_wisata: ArrayList<String>? = null,
    var tempat_wisata_data: ArrayList<TempatWisataItem>? = null,
    var produk: List<ProdukPaketWisata>? = null,
    var waktu_perjalanan: WaktuPerjalanan? = null,
): Serializable

data class WaktuPerjalanan(
    var hari: Int = 0,
    var malam: Int = 0
)

