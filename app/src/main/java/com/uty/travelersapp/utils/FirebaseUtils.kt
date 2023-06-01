package com.uty.travelersapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val firebaseDatabase = FirebaseFirestore.getInstance()
    val firebaseStorage = Firebase.storage.reference
}