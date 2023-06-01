package com.uty.travelersapp.models

import java.io.Serializable

data class TempatWisataItem(
    var id: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val deskripsi: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    var foto: ArrayList<FotoModel>? = null,
    var foto_thumb: FotoModel? = null,
    var thumbnail_foto: String? = null
): Serializable
