package com.uty.travelersapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.models.FotoModel
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase

class TempatWisataRepository {

    private val dbCol = firebaseDatabase.collection("tempat_wisata")

    @Volatile private var INSTANCE: TempatWisataRepository? = null
    fun getInstance(): TempatWisataRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = TempatWisataRepository()
            INSTANCE = instance
            instance
        }
    }
    fun getAllTempatWisata(tempatWisataList: MutableLiveData<List<TempatWisataItem>>) {
        val _twList: ArrayList<TempatWisataItem> = arrayListOf()
        dbCol.orderBy("created_at", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
            val documents = result.documents

            for (document in documents) {
                var tempatWisata = document.toObject<TempatWisataItem>()!!
                tempatWisata.id = document.id

                _twList.add(tempatWisata)
                tempatWisataList.postValue(_twList)
            }

        }
    }

    fun getDetailTempatWisata(tempatWisata: MutableLiveData<TempatWisataItem>, id: String) {
        dbCol.document(id).get()
            .addOnSuccessListener { doc ->
                val tw = doc.toObject(TempatWisataItem::class.java)!!
                tw.id = doc.id
                doc.reference.collection("foto").get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val results = task.result
                            val fotos = ArrayList<FotoModel>()
                            for (document in results.documents) {
                                val f = document.toObject(FotoModel::class.java)!!
                                fotos.add(f)
                            }
                            tw.foto = fotos

                        }
                        tempatWisata.postValue(tw)
                    }
            }
    }
}