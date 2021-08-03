package com.example.splash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splash.repository.HomeRepository
import com.example.splash.model.Tweet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel@Inject constructor(private val repository: HomeRepository) : ViewModel() {
    var tweetListLiveData: MutableLiveData<List<Tweet>>? = repository.tweets
    val progress = MutableLiveData<Boolean>(false)

    init {
        repository.addSnapShotListener()
        getTweets()
    }

    private fun getTweets() {
        progress.value = true
        repository.getTweets()
    }

    fun addTweet(tweetText: String) {
        progress.value = true
        repository.addTweet(tweetText)
    }

    fun updateTweet(tweetText: String, id: String) {
        progress.value = true
        repository.updateTweet(tweetText, id)
    }

    fun deleteTweet(id: String) {
        progress.value = true
        repository.deleteTweet(id)
    }
}