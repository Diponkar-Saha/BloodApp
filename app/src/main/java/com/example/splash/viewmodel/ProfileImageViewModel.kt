package com.example.splash.viewmodel

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splash.repository.FirebaseAuthRepository
import com.example.splash.utilites.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileImageViewModel@Inject constructor(private val repository: FirebaseAuthRepository) : ViewModel() {

    val progress = MutableLiveData<Boolean>()
    var userLiveData: MutableLiveData<FirebaseUser>? = repository.userLiveData

    private val _uploadStatus = MutableLiveData<Resource<Unit>>()
    val uploadStatus: LiveData<Resource<Unit>> = _uploadStatus

    fun uploadImageData(date: String, imageUri: Uri) {
        progress.value  = true
        repository.uploadImageData(date,imageUri)
    }
    fun upload(imageUri: Uri,date: String) = viewModelScope.launch {
        repository.updateUser(imageUri ,date).collect {
            _uploadStatus.postValue(it)
        }
    }



}