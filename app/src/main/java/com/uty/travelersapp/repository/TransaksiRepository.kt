package com.uty.travelersapp.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.models.Transaksi
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import com.uty.travelersapp.utils.Helper
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import com.uty.travelersapp.models.Response.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class TransaksiRepository {
    private val dbCol = firebaseDatabase.collection("transaksi")

    @Volatile private var INSTANCE: TransaksiRepository? = null

    fun getInstance(): TransaksiRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = TransaksiRepository()
            INSTANCE = instance
            instance
        }
    }

    fun insertTransaksi(userId: String, dataTransaksi: Transaksi) = flow {
        emit(Loading())
        val result = dbCol.add(dataTransaksi).await()
        emit(Success(result.id))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getUserTransaksi(userId: String) = flow {
        emit(Loading())
        val _transaksi = arrayListOf<Transaksi>()
        val result = dbCol.whereEqualTo("user.id", userId).orderBy("created_at", Query.Direction.DESCENDING).get().await()
        for (doc in result.documents) {
            Log.d("kencana", "transaksi ${userId}: " + doc.toString())
            val transaksi = doc.toObject(Transaksi::class.java)
            _transaksi.add(transaksi!!)
        }
        emit(Success(_transaksi))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getDetailTransaksi(idTransaksi: String)  = flow{
        emit(Loading())
        val result = dbCol.document(idTransaksi).get().await()
        val transaksi = result.toObject(Transaksi::class.java)
        emit(Success(transaksi))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getDetailTransaksiRealtime(idTransaksi: String) = callbackFlow {
        trySend(Response.Loading())
        val registration = dbCol.document(idTransaksi).addSnapshotListener { snapshot, error ->
            if (error != null) {
//                close(Response.Failure(error.message))
                trySend(Response.Failure(error.message.toString()))
                return@addSnapshotListener
            }

            snapshot?.let {
                if (it.exists()) {
                    val transaction = it.toObject(Transaksi::class.java)
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