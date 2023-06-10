package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId

data class JenisArmada(
    @DocumentId
    val id: String? = "",
    val nama: String? = "",
    val kapasitas_min: Int? = 0,
    val kapasitas_max: Int? = 0,
    val foto: String? = ""
)
