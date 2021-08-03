package com.example.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splash.repository.FirebaseAuthRepository

class FirebaseAuthViewModelFactory(private val repository: FirebaseAuthRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirebaseAuthViewModel(repository) as T
    }

}