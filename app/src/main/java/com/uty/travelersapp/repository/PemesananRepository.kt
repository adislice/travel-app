package com.uty.travelersapp.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.uty.travelersapp.models.JenisKendaraan
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Pemesanan
import com.uty.travelersapp.models.PemesananInsert
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.Promo
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

    fun insertPemesanan(userId: String, dataPemesanan: PemesananInsert) = flow {
        emit(Loading())
        val docRef = dbCol.document()
        dataPemesanan.id = docRef.id
        val result = docRef.set(dataPemesanan).await()
        emit(Success(docRef.id))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getUserPemesanan(userId: String) = flow {
        emit(Loading())
        val _pemesanan = arrayListOf<Pemesanan>()
        val result = dbCol.whereEqualTo("user_id", userId).orderBy("created_at", Query.Direction.DESCENDING).get().await()
        for (doc in result.documents) {
            Log.d("kencana", "pemesanan ${userId}: " + doc.toString())
            val pemesanan = doc.toObject(Pemesanan::class.java)
            pemesanan?.id = doc.id
            pemesanan?.paket_wisata_id?.let { pwId ->
                val pw = firebaseDatabase.collection("paket_wisata").document(pwId).get().await()
                val pwData = pw.toObject(PaketWisataItem::class.java)
                pemesanan?.paket_wisata_foto = pwData?.foto?.firstOrNull()
                pemesanan?.paket_wisata_nama = pwData?.nama

                pemesanan?.produk_id?.let {produkId ->
                    val produk = firebaseDatabase.collection("paket_wisata")
                        .document(pwId)
                        .collection("produk")
                        .document(produkId)
                        .get()
                        .await()
                    val produkData = produk.toObject(ProdukPaketWisata::class.java)
                    pemesanan?.produk_harga = produkData?.harga
                    produkData?.jenis_kendaraan_id?.let { jkId ->
                        val jk = firebaseDatabase.collection("jenis_kendaraan").document(jkId).get().await()
                        val jkData = jk.toObject(JenisKendaraan::class.java)
                        pemesanan?.jenis_kendaraan_nama = jkData?.nama
                        pemesanan?.jenis_kendaraan_jumlah_seat = jkData?.jumlah_seat
                    }
                }
            }

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
        pemesanan?.id = result.id
        emit(Success(pemesanan))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }

    fun getDetailPemesananRealtime(idPemesanan: String) = callbackFlow {
        trySend(Loading())
        val registration = dbCol.document(idPemesanan).addSnapshotListener { snapshot, error ->
            if (error != null) {
//                close(Response.Failure(error.message))
                trySend(Failure(error.message.toString()))
                return@addSnapshotListener
            }

            snapshot?.let {
                if (it.exists()) {
                    val pemesanan = it.toObject(Pemesanan::class.java)
                    pemesanan?.id = it.id
                    pemesanan?.let { pemesananData ->
                        pemesanan?.paket_wisata_id?.let { pwId ->
                            firebaseDatabase.collection("paket_wisata").document(pwId).get().addOnSuccessListener { pw ->
                                val pwData = pw.toObject(PaketWisataItem::class.java)
                                pemesanan?.paket_wisata_foto = pwData?.foto?.firstOrNull()
                                pemesanan?.paket_wisata_nama = pwData?.nama
                                pemesanan?.produk_id?.let { produkId ->
                                    firebaseDatabase.collection("paket_wisata")
                                        .document(pwId)
                                        .collection("produk")
                                        .document(produkId)
                                        .get().addOnSuccessListener { produkDoc ->
                                            val produkData =
                                                produkDoc.toObject(ProdukPaketWisata::class.java)
                                            pemesanan?.produk_harga = produkData?.harga
                                            produkData?.jenis_kendaraan_id?.let { jkId ->
                                                firebaseDatabase.collection("jenis_kendaraan")
                                                    .document(jkId).get()
                                                    .addOnSuccessListener { jk ->
                                                        val jkData =
                                                            jk.toObject(JenisKendaraan::class.java)
                                                        pemesanan?.jenis_kendaraan_nama =
                                                            jkData?.nama
                                                        pemesanan?.jenis_kendaraan_jumlah_seat =
                                                            jkData?.jumlah_seat
//                                                        trySend(Success(pemesanan))
                                                        pemesanan?.promo_id?.let { promoId ->
                                                            firebaseDatabase.collection("promo").document(promoId).get().addOnSuccessListener { promo ->
                                                                val promoData = promo.toObject(Promo::class.java)
                                                                Log.d("kencana", promoData.toString())
                                                                pemesanan.promo_kode = promoData?.kode
                                                                pemesanan.promo_nama = promoData?.nama
                                                                pemesanan.promo_persen = promoData?.persen
                                                                trySend(Success(pemesanan))
                                                            }
                                                        }
                                                        trySend(Success(pemesanan))
                                                    }

                                            }
                                        }
                                }
                            }
                        }

                        trySend(Success(pemesananData))
                    }
                }
            }
        }

        awaitClose {
            registration.remove()
        }
    }
}