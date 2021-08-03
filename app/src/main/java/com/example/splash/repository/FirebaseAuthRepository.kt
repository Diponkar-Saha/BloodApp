package com.example.splash.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.splash.commons.TwitterCloneAppData
import com.example.splash.model.Postss
import com.example.splash.model.UserData
import com.example.splash.utilites.Constants
import com.example.splash.utilites.Constants.Companion.USERS
import com.example.splash.utilites.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import io.grpc.InternalChannelz.id
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

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







    fun createUser(name: String, email: String, phone: String, password: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    authentication.createUserWithEmailAndPassword(email, password).await()
                response.user?.let {
                    uploadUserData(name, email, phone)
                    // val token = getUserFcmToken()
                    val user = UserData(id = it.uid, name = name, email = email)
                    //user.deviceToken = token
                    sharedPref.saveUserData(user)
                    //firebaseAuth = FirebaseAuth.getInstance()


                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    authentication.signOut()
                    Log.d(TAG, "Error adding document", e)
                }


            }
//                else {
//                    Toast.makeText(
//                        appContext.applicationContext,
//                        "Registration Failure: " + it.exception?.message,
//                        Toast.LENGTH_SHORT
//                    ).show();
//                    userLiveData?.postValue(null)
//                }
            //           })
        }


    fun loginUser(email: String, password: String) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = authentication.currentUser
                    user?.uid?.let {
                        db.collection(Constants.USERS)
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    if (document.get("id") == it) {
                                        var userData = document.toObject(UserData::class.java)
                                        TwitterCloneAppData.setUserData(userData)
                                        break
                                    }
                                }
                            }
                    }
                    loginUserLiveData?.postValue(user)
                } else {
                    Toast.makeText(
                        appContext.applicationContext,
                        "Login Failure: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show();
                    loginUserLiveData?.postValue(null)
                }
            }

    }


    private fun uploadUserData(name: String, email: String, phone: String) {
        var user = HashMap<String, String>()
        authentication.currentUser?.uid?.let {
            user["id"] = it
            user["name"] = name
            user["phone"] = phone
            user["email"] = email

            db.collection(Constants.USERS)
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "firebaseRepository",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                    var userData: UserData = UserData(it, name, phone, email)
                    TwitterCloneAppData.setUserData(userData)
                    userLiveData?.postValue(authentication.currentUser)
                }
                .addOnFailureListener { e ->
                    Log.w("firebaseRepository", "Error adding document", e)
                    Toast.makeText(
                        appContext.applicationContext,
                        "Registration Failure: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show();
                    userLiveData?.postValue(null)
                }
        }

    }

//    suspend fun getCurrentUserDetails(
//        successListener: (UserData) -> Unit,
//        failureListener: (Exception) -> Unit
//    ) {
//        withContext(Dispatchers.IO)
//        {
//            db.collection(USERS).document(getCurrentUserId())
//                .addSnapshotListener { users, error ->
//                    if (error != null) {
//                        failureListener.invoke(error)
//                        return@addSnapshotListener
//                    }
//                    users.let {
//                        val user = users?.toObject<UserData>()
//                        Timber.d(user.toString())
//                        if (user != null) {
//                            successListener.invoke(user)
//                        }
//                    } ?: failureListener.invoke(Exception("User not found"))
//
//                }
//        }
//    }

    fun uploadImageData(date: String, imageUri: Uri) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val imageUrl = storage.reference.child("images/$fileName").putFile(imageUri!!)
                .await().storage.downloadUrl.await().toString()
            val user = getUserDetails()
//            user.image = imageUrl
//            user.date = date
//            updateUserDetailsInFireStore(user)

//            val user = hashMapOf(
//                "date" to date,
//                "last" to imageUrl,
//            )
//            fireStore.collection(USER_COLLECTION).document(user.id)
//                        .update("username", user.username, "bio", user.bio, "profileImg", user.profileImg)
//                        .await()

            db.collection(Constants.USERS)
                .document(user.id)
                .update( "date",date,
                    "last",imageUrl,)
                .addOnSuccessListener {
                    //  Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Error adding document", e)

            }


        }
    }

    private suspend fun updateUserDetailsInFireStore(user: UserData) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection(Constants.USERS).document(user.id)
                    .update("date", user.date, "profileImg", user.image)
                    .await()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Error adding document", e)

                }


            }
        }



    ////////////////////


    suspend fun updateUser(image: Uri, date: String) =
        flow<Resource<Unit>> {
            emit(Resource.Loading())
            val user = getUserDetails()
            val imageUrl = uploadUserImageToStorage(image, user.id)
            user.date = date
            user.image = imageUrl
            updateUserDetailsInFireStor(user)
            sharedPref.saveUserData(user)
            emit(Resource.Success())
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }

    private suspend fun uploadUserImageToStorage(image: Uri, filename: String): String {
        val response = storage.reference.child("images/$fileName").putFile(image).await()
        return storage.reference.child(filename).downloadUrl.await().toString()
    }



    private suspend fun updateUserDetailsInFireStor(user: UserData) {
        db.collection(USERS).document(user.id)
            .update("date", user.date, "image", user.image)
            .await()
    }
}





