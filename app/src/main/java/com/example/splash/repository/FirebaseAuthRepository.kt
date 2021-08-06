package com.example.splash.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.splash.model.UserData
import com.example.splash.utilites.Constants.Companion.USERS
import com.example.splash.utilites.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

private const val TAG = "TESTE"
class FirebaseAuthRepository @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val sharedPref: Preferences,
    @ApplicationContext private val appContext: Context
) {
    var userLiveData: MutableLiveData<FirebaseUser>? = MutableLiveData()
    var loginUserLiveData: MutableLiveData<FirebaseUser>? = MutableLiveData()
    fun getUserDetails() = sharedPref.getUserData()
    private val fileName = UUID.randomUUID().toString()






    suspend fun createUser(
         name: String, email: String, phone: String, password: String
    ) = flow<Resource<UserData>> {
        emit(Resource.Loading())
        val response = authentication.createUserWithEmailAndPassword(email, password).await()
        response.user?.let {
           // val token = getUserFcmToken()
            val user = UserData(id = it.uid, name = name, email = email,phone = phone)
           // user.deviceToken = token
            saveUserInFireStore(user)
            sharedPref.saveUserData(user)
            emit(Resource.Success(user))
        } ?: emit(Resource.Error())
    }.catch {
        authentication.signOut()
        emit(Resource.Error(it.message.toString()))
    }




    suspend fun loginUser(email: String, password: String) = flow<Resource<UserData>> {
        emit(Resource.Loading())
        val response = authentication.signInWithEmailAndPassword(email, password).await()
        response.user?.let {
            val userId = it.uid
            val user = getUserDataFromFireStore(userId)
            sharedPref.saveUserData(user = user.data!!)
            emit(user)
        } ?: emit(Resource.Error())
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }








    suspend fun getUserDataFromFireStore(userId: String): Resource<UserData> {
        val user =
            db.collection(USERS).document(userId).get().await().toObject<UserData>()
        return user?.let {
            Resource.Success(it)
        } ?: Resource.Error()

    }

    private suspend fun saveUserInFireStore(user: UserData) =
        db.collection(USERS).document(user.id).set(user).await()




    ////////////////////
    suspend fun bloodGroupUpload(blood: String) =
        flow<Resource<Unit>> {
            emit(Resource.Loading())
            val user = getUserDetails()
            user.bloodGroup = blood
            updateUserBloodInFireStor(user)
            sharedPref.saveUserData(user)
            emit(Resource.Success())
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }

    private fun updateUserBloodInFireStor(user: UserData) {
        db.collection(USERS).document(user.id)
            .update("bloodGroup",user.bloodGroup )

    }


    suspend fun updateUser(image: Uri, date: String) =
        flow<Resource<Unit>> {
            emit(Resource.Loading())
            val user = getUserDetails()
            val imageUrl = uploadUserImageToStorage(image, user.id)
            Log.d(TAG, "image url ::::::    :$imageUrl")
            Log.d(TAG, "image url ::::::    :$date")

            user.date = date
            user.image = imageUrl
            updateUserDetailsInFireStor(user)
            sharedPref.saveUserData(user)
            emit(Resource.Success())
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }

    private suspend fun uploadUserImageToStorage(image: Uri, filename: String): String {
//        val response = storage.reference.child("images/$fileName").putFile(image).await()
//        return storage.reference.child(filename).downloadUrl.await().toString()
        val imageUrl= storage.reference.child("images/$fileName").putFile(image).await().storage.downloadUrl.await().toString()
        return imageUrl
        Log.d(TAG, "image url nicher ta  ::::::    :$imageUrl")

    }



//    database().ref().child('/posts/' + newPostKey)
//    .set({ title: "New title", body: "This is the new body" });
    private fun updateUserDetailsInFireStor(user: UserData) {
        db.collection(USERS).document(user.id)
            .update("date",user.date ,"image",user.image)
    }
}








