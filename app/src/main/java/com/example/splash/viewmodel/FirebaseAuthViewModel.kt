package com.example.splash.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splash.model.UserData
import com.example.splash.repository.FirebaseAuthRepository
import com.example.splash.utilites.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(private val repository: FirebaseAuthRepository) : ViewModel() {
    var userLiveData: MutableLiveData<FirebaseUser>? = repository.userLiveData
    var loginUserLiveData: MutableLiveData<FirebaseUser>? = repository.loginUserLiveData

    val progress = MutableLiveData<Boolean>()
    private val _registerState = MutableLiveData<Resource<UserData>>()
    val registerState: LiveData<Resource<UserData>> = _registerState


    fun createUser(name: String, email: String, phone: String, password: String) = viewModelScope.launch {
       // progress.value  = true
        repository.createUser(name, email, phone, password).collect { status ->
            _registerState.postValue(status)
        }
    }

    private val _loginState = MutableLiveData<Resource<UserData>>()
    val loginState: LiveData<Resource<UserData>> = _loginState

    fun loginUser( email: String, password: String)= viewModelScope.launch   {

        repository.loginUser(email, password).collect {
            _loginState.postValue(it)
        }
    }

}