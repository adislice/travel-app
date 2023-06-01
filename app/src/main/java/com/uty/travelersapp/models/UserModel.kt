package com.uty.travelersapp.models

import com.google.firebase.Timestamp

data class UserModel(
    var id: String? = null,
    val nama: String? = null,
    val email: String? = null,
    val no_telp: String? = null,
    val profile_picture: String? = null,
    val created_at: Timestamp? = null
)
