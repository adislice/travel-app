package com.uty.travelersapp.models

import java.io.Serializable

data class TempatWisataArrayItem(
    var order: Integer? = null,
    var tempat_wisata_id: String? = null,
    var tempat_wisata_data: TempatWisataItem? = null
): Serializable
