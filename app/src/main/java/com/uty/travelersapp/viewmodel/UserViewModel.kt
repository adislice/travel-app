package com.uty.travelersapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.UserModel
import com.uty.travelersapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class UserViewModel: ViewModel() {
    private val repository: UserRepository
    private val _userDetail = MutableLiveData<UserModel>()
    private var initialized = false
    var userDetail: LiveData<UserModel> = _userDetail
        get() = field

    val userId = MutableLiveData<String>()
    val shouldRefresh = MutableLiveData(false)

    init {
        repository = UserRepository().getInstance()
    }

    var userDetailData = userId.distinctUntilChanged().switchMap { user ->
        Log.d("kencana", "user data changed")
        return@switchMap liveData(Dispatchers.IO) {
            repository.getDetailUser(user).collect { response ->
                emit(response)
            }

        }
    }

    fun setUser(userId: String) {
        Log.d("kencana", "set user to: " + userId + " from: " + this.userId.value)
        this.userId.value = userId
    }

    fun updateUser(userId:String, nama: String, email: String, noTelp: String, profilePic: Uri, fileName: String) = liveData (Dispatchers.IO) {
        Log.d("kencana", "update profile vm")
        repository.updateUser(userId, nama, email, noTelp, profilePic, fileName).collect { response ->
            emit(response)
        }

    }
}