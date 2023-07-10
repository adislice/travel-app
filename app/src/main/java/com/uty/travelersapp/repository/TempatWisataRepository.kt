package com.uty.travelersapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.models.FotoModel
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

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

    fun getAllTempatWisataRealtime(lim: Int = 0, search: String = ""): Flow<MutableList<TempatWisataItem>> {
        val uppercaseString = if (search.isNotEmpty()) {
            search.get(0).uppercase() + search.slice(1 until search.length)
        } else {
            ""
        }
        return dbCol.orderBy("nama", Query.Direction.ASCENDING).where(Filter.or(
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", search),
                Filter.lessThanOrEqualTo("nama", search + "\uf8ff")
            ),
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", uppercaseString),
                Filter.lessThanOrEqualTo("nama", uppercaseString + "\uf8ff")
            ),
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", search.lowercase()),
                Filter.lessThanOrEqualTo("nama", search.lowercase() + "\uf8ff")
            )
        ))
            .snapshots()
            .map { snapshot ->
                Log.d("kencana", "new tempat wisata fetched")
                return@map snapshot.toObjects(TempatWisataItem::class.java)
            }
    }

    fun getAllTempatWisataSearchold(limit: Int = 0, search: String = ""): Flow<MutableList<TempatWisataItem>> {
        Log.d("kencana", "search " + search.get(0).uppercase() + search.slice(1..search.length))
        val uppercaseString = if (search.length > 0) {
            search.get(0).uppercase() + search.slice(1..search.length)
        } else {
            ""
        }
        var query = dbCol.orderBy("created_at", Query.Direction.DESCENDING)
        if (search.isNotEmpty()) {
            query = query.whereEqualTo("nama", search)
        }
        val query2 = dbCol.where(Filter.or(
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", search),
                Filter.lessThanOrEqualTo("nama", search + "\uf8ff")
            ),
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", uppercaseString),
                Filter.lessThanOrEqualTo("nama", uppercaseString + "\uf8ff")
            ),
            Filter.and(
                Filter.greaterThanOrEqualTo("nama", search.lowercase()),
                Filter.lessThanOrEqualTo("nama", search.lowercase() + "\uf8ff")
            )
        ))
        return query2.snapshots().map { snapshot ->
                Log.d("kencana", "new tempat wisata fetched")
                Log.d("kencana", "result: " + snapshot.documents.toString())
                return@map snapshot.toObjects(TempatWisataItem::class.java)
            }
    }

    fun getTempatWisataHome() = dbCol.limit(5).snapshots().map { snapshot ->
        snapshot.toObjects(TempatWisataItem::class.java)
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
//                doc.reference.collection("foto").get()
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val results = task.result
//                            val fotos = ArrayList<FotoModel>()
//                            for (document in results.documents) {
//                                val f = document.toObject(FotoModel::class.java)!!
//                                fotos.add(f)
//                            }
//                            tw.foto = fotos
//
//                        }
                tempatWisata.postValue(tw)
//                    }
            }
    }


}