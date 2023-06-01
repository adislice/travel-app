package com.uty.travelersapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils
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
//    fun getAllTujuanWisata(tujuanWisataList: MutableLiveData<ArrayList<TujuanWisataItem>>, docId: String) {
//        val _twList = ArrayList<TujuanWisataItem>()
//        dbCol.document(docId).get()
//            .addOnSuccessListener {result ->
//                val twList: List<TujuanWisataItem> = result.toObject(TempatWisataList::class.java)!!.tempat_wisata_list!!
//                for (tw in twList) {
//                    val _tw = tw
//
//                    tw.tempat_wisata?.get()
//                        ?.addOnSuccessListener { docRes ->
//                            _tw.nama = docRes.getString("nama")
//                            _tw.tempat_wisata_id = docRes.id
//                            _tw.tempat_wisata_data = docRes.toObject(TempatWisataItem::class.java)!!
//                            docRes.reference.collection("foto").orderBy("nama").limit(1).get()
//                                .addOnSuccessListener {docsRes ->
//                                    val foto = docsRes.documents[0].toObject(FotoModel::class.java)
//                                    _tw.foto_thumb = foto
//                                    _twList.add(_tw)
//                                    tujuanWisataList.postValue(_twList)
//                                }
//                        }
//                }
//            }
//    }

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
//    fun getAllTujuanWisataNew(docId: String): {
//        val _twList = ArrayList<TujuanWisataItem>()
//        val result = dbCol.document(docId).get()
//            .addOnSuccessListener {result ->
//                val twList: List<TujuanWisataItem> = result.toObject(TempatWisataList::class.java)!!.tempat_wisata_list!!
//                for (tw in twList) {
//                    val _tw = tw
//                    val docRes = tw.tempat_wisata?.get()
//                        ?.addOnSuccessListener { docRes ->
//                            _tw.nama = docRes.getString("nama")
//                            docRes.reference.collection("foto").orderBy("nama").limit(1).get()
//                                .addOnSuccessListener {docsRes ->
//                                    val foto = docsRes.documents.get(0).toObject(FotoModel::class.java)
//                                    _tw.foto_thumb = foto
//                                    _twList.add(_tw)
//
//                                }
//                        }
//                }
//            }
//        return _twList
//    }
}