package com.uty.travelersapp.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.uty.travelersapp.utils.FirebaseUtils
import kotlinx.coroutines.flow.flow
import com.uty.travelersapp.models.Response.*
import com.uty.travelersapp.models.UserModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val dbCol = FirebaseUtils.firebaseDatabase.collection("users")

    @Volatile private var INSTANCE: UserRepository? = null
    fun getInstance(): UserRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = UserRepository()
            INSTANCE = instance
            instance
        }
    }

    fun getDetailUser(userId: String) = flow {
        emit(Loading())
        val query = dbCol.document(userId)
        val result = query.get().await()
        val userData = result.toObject(UserModel::class.java)!!
        userData.id = result.id
        Log.d("kencana", "get user: " + userId)
        emit(Success(userData))
    }.catch { error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }


    fun updateUser(userId:String, nama: String, email: String, noTelp: String, profilePic: Uri, fileName: String)  = flow {
        Log.d("kencana", "update profile repo")
        emit(Loading())
        var ppUrl = ""
        if (profilePic != Uri.EMPTY) {
            val userPicRef = FirebaseUtils.firebaseStorage.child("images/users/${fileName}")
            val res = userPicRef.putFile(profilePic).await()
            if (res.task.isSuccessful) {
                ppUrl = userPicRef.downloadUrl.await().toString()
            }
        }

        val userRef = dbCol.document(userId)
        val updateData = mutableMapOf(
            "nama" to nama,
            "email" to email,
            "no_telp" to noTelp,
        )
        if (ppUrl.isNotEmpty()) {
            updateData.put("profile_picture", ppUrl)
        }
        val result = userRef.update(updateData as Map<String, Any>).await()

        emit(Success(true))
    }.catch {error ->
        error.message?.let { errorMessage ->
            emit(Failure(errorMessage))
        }
    }
}