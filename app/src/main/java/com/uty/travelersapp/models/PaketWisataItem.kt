package com.uty.travelersapp.models

import java.io.Serializable

data class PaketWisataItem (
    var id: String? = null,
    val nama: String? = null,
    val deskripsi: String? = null,
    val thumbnail_foto: String? = null,
    var tempat_wisata: List<TempatWisataArrayItem>? = null
): Serializable

