package com.uty.travelersapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.models.JenisKendaraan
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PaketWisataRepository {
    private val dbCol = FirebaseUtils.firebaseDatabase.collection("paket_wisata")


    @Volatile private var INSTANCE: PaketWisataRepository? = null
    fun getInstance(): PaketWisataRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = PaketWisataRepository()
            INSTANCE = instance
            instance
        }
    }

    fun getAllPaketWisata(paketWisataList: MutableLiveData<List<PaketWisataItem>>) {
        val _pwList: ArrayList<PaketWisataItem> = arrayListOf()
        dbCol.get().addOnSuccessListener { result ->
            val documents = result.documents
            for (document in documents) {
                var paketWisata = document.toObject(PaketWisataItem::class.java)!!
                paketWisata.id = document.id
                Log.d("firebase", "Paket wisata " + document.data.toString())
                _pwList.add(paketWisata)
            }
            paketWisataList.postValue(_pwList)
        }
    }

    fun getAllPaketWisataRealtime(limit: Int = 0) = dbCol.apply{
        orderBy("created_at", Query.Direction.DESCENDING)
        if (limit != 0) {
            limit(limit.toLong())
        }
    }.snapshots().map { snapshot ->
        val pwList = arrayListOf<PaketWisataItem>()
        for (snap in snapshot.documents) {
            var doc = snap.toObject(PaketWisataItem::class.java)
            var resProduk = snap.reference.collection("produk").get().await()
            var listProduk = resProduk.toObjects(ProdukPaketWisata::class.java)
            for (produk in listProduk) {
                val jenisKendaraanRef = FirebaseUtils.firebaseDatabase.collection("jenis_kendaraan").document(produk.jenis_kendaraan_id!!)

                val armadaResult = jenisKendaraanRef?.get()?.await()
                if (armadaResult != null) {
                    if (armadaResult.exists()) {
                        produk.jenis_kendaraan = armadaResult.toObject(JenisKendaraan::class.java)
                    }
                }
            }
            doc?.produk = listProduk
            val twList = arrayListOf<TempatWisataItem>()
            for (twId in doc?.tempat_wisata!!) {
                var _tw = FirebaseUtils.firebaseDatabase.collection("tempat_wisata").document(twId).get().await()
                var tw = _tw.toObject(TempatWisataItem::class.java)
                twList.add(tw!!)
            }
            doc.tempat_wisata_data = twList
            pwList.add(doc)
        }
        return@map pwList
    }

    fun getAllTujuanWisataByModel(tujuanWisataList: MutableLiveData<ArrayList<TempatWisataArrayItem>>, tempat_wisata_list: List<String>) {
        val _twList = ArrayList<TempatWisataArrayItem>()
        val twCol = FirebaseUtils.firebaseDatabase.collection("tempat_wisata")
        tempat_wisata_list.forEachIndexed { index, twItem ->
            Log.d("firebase", "Getting data: " + twItem)
            twCol.document(twItem).get()
                .addOnSuccessListener { result ->
                    val _tw = result.toObject(TempatWisataItem::class.java)!!
                    val newData = TempatWisataArrayItem(order = index+1, tempat_wisata_id = twItem, tempat_wisata_data = _tw)
                    _twList.add(newData)
                    tujuanWisataList.postValue(_twList)
                }
        }
        for (twItem in tempat_wisata_list) {


        }
    }


    fun getProdukPaketWisata(idPaket: String) = flow {
        val _produkList = ArrayList<ProdukPaketWisata>()
        emit(Response.Loading())
        val produkCol = dbCol.document(idPaket).collection("produk").whereEqualTo("is_deleted", false)
        val result = produkCol.get().await()
        result.documents.forEach { doc ->
            val produkObject = doc.toObject(ProdukPaketWisata::class.java)!!
            val jenisKendaraanRef = FirebaseUtils.firebaseDatabase.collection("jenis_kendaraan").document(produkObject.jenis_kendaraan_id!!)
            val jenisPaketDoc = jenisKendaraanRef.get()?.await()!!
            if (jenisPaketDoc.exists()){
                val jenisObject = jenisPaketDoc.toObject(JenisKendaraan::class.java)
                produkObject.jenis_kendaraan = jenisObject
            }
            _produkList.add(produkObject)
        }
        emit(Response.Success(_produkList))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Response.Failure(errorMessage))
        }
    }

    fun getDetailPaketWisata(idPaket: String) = flow {
        val paketDocRef = dbCol.document(idPaket).get().await()
        if (paketDocRef.exists()) {
            val data = paketDocRef.toObject(PaketWisataItem::class.java)!!
            data.id = paketDocRef.id
            emit(Response.Success(data))
        } else {
            emit(Response.Failure("Paket Wisata Tidak Ditemukan"))
        }
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Response.Failure(errorMessage))
        }
    }
}