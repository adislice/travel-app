package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class PaketWisataItem (
    @DocumentId
    var id: String? = "",
    val nama: String? = null,
    val deskripsi: String? = null,
    val thumbnail_foto: String? = null,
    var tempat_wisata: List<TempatWisataArrayItem>? = null
): Serializable

