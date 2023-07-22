package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId

data class JenisKendaraan(
    @DocumentId
    var id: String? = "",
    val nama: String? = "",
    val jumlah_seat: Int? = 0,
)
