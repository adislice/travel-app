package com.uty.travelersapp.models

import java.io.Serializable

data class TempatWisataModel (
    val nama: String? = "",
    val gambar: String? = "",
    val deskripsi: String? = "",
    val longitude: String? = "",
    val latitude: String? = ""
): Serializable