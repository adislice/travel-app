package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class ProdukPaketWisata (
    @DocumentId
    var id: String? = "",
    val nama: String? = "",
    val harga: Double? = 0.0,
    val is_aktif: Boolean? = false,
    val jenis_armada_ref: DocumentReference? = null,
    var jenis_armada: JenisArmada? = null,
)