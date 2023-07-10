package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class TempatWisataItem(
    @DocumentId var id: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val kota: String? = null,
    val provinsi: String? = null,
    val deskripsi: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    var foto: ArrayList<String>? = null,
    var thumbnail_foto: String? = null
): Serializable
