package com.uty.travelersapp.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Pemesanan
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import kotlinx.coroutines.flow.flow
import com.uty.travelersapp.models.Response.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class PemesananRepository {
    private val dbCol = firebaseDatabase.collection("pemesanan")

    @Volatile private var INSTANCE: PemesananRepository? = null

    fun getInstance(): PemesananRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = PemesananRepository()
            INSTANCE = instance
            instance
        }
    }

    fun insertPemesanan(userId: String, dataPemesanan: Pemesanan) = flow {
        emit(Loading())
        val result = dbCol.add(dataPemesanan).await()
        emit(Success(result.id))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getUserPemesanan(userId: String) = flow {
        emit(Loading())
        val _pemesanan = arrayListOf<Pemesanan>()
        val result = dbCol.whereEqualTo("user.id", userId).orderBy("created_at", Query.Direction.DESCENDING).get().await()
        for (doc in result.documents) {
            Log.d("kencana", "pemesanan ${userId}: " + doc.toString())
            val pemesanan = doc.toObject(Pemesanan::class.java)
            _pemesanan.add(pemesanan!!)
        }
        emit(Success(_pemesanan))
    }.catch { error ->
        error.message?.let { errorMessage ->
            Log.e("kencana", errorMessage)
            emit(Failure(errorMessage))
        }
    }

    fun getDetailPemesanan(idPemesanan: String)  = flow{
        emit(Loading())
        val result = dbCol.document(idPemesanan).get().await()
        val pemesanan = result.toObject(Pemesanan::class.java)
        emit(Success(pemesanan))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getDetailPemesananRealtime(idPemesanan: String) = callbackFlow {
        trySend(Response.Loading())
        val registration = dbCol.document(idPemesanan).addSnapshotListener { snapshot, error ->
            if (error != null) {
//                close(Response.Failure(error.message))
                trySend(Response.Failure(error.message.toString()))
                return@addSnapshotListener
            }

            snapshot?.let {
                if (it.exists()) {
                    val transaction = it.toObject(Pemesanan::class.java)
                    transaction?.let { transactionData ->
                        trySend(Response.Success(transactionData))
                    }
                }
            }
        }

        awaitClose {
            registration.remove()
        }
    }
}