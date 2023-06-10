package com.uty.travelersapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.uty.travelersapp.models.JenisArmada
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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

    fun getAllTujuanWisataByModel(tujuanWisataList: MutableLiveData<ArrayList<TempatWisataArrayItem>>, tempat_wisata_list: List<TempatWisataArrayItem>) {
        val _twList = ArrayList<TempatWisataArrayItem>()
        val twCol = FirebaseUtils.firebaseDatabase.collection("tempat_wisata")
        for (twItem in tempat_wisata_list) {
            Log.d("firebase", "Getting data: " + twItem.tempat_wisata_id)
            twCol.document(twItem.tempat_wisata_id!!).get()
                .addOnSuccessListener { result ->
                    val _tw = result.toObject(TempatWisataItem::class.java)!!
                    val newData = twItem
                    newData.tempat_wisata_data = _tw
                    _twList.add(newData)
                    tujuanWisataList.postValue(_twList)
                }

        }
    }


    fun getProdukPaketWisata(idPaket: String) = flow {
        val _produkList = ArrayList<ProdukPaketWisata>()
        emit(Response.Loading())
        val produkCol = dbCol.document(idPaket).collection("produk")
        val result = produkCol.get().await()
        result.documents.forEach { doc ->
            val produkObject = doc.toObject(ProdukPaketWisata::class.java)!!
            val jenisPaketDoc = produkObject.jenis_armada_ref?.get()?.await()!!
            if (jenisPaketDoc.exists()){
                val jenisObject = jenisPaketDoc.toObject(JenisArmada::class.java)
                produkObject.jenis_armada = jenisObject
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