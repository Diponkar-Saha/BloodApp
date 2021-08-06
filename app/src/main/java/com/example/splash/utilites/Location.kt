package com.example.splash.utilites

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Location {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    fun getCurrentLocation(mMap : GoogleMap, context : Context) = CoroutineScope(Dispatchers.IO).launch{
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            val location= fusedLocationProviderClient.lastLocation

            val currentLocation = LatLng(location.await().latitude,location.await().longitude)
            withContext(Dispatchers.Main){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,18F))
                Log.d(TAG, "getCurrentLocation: Success")
            }
        }catch (e : SecurityException){
            Log.i(TAG, "getCurrentLocation: Error getting current location: ${e.message} ")
        }
    }

}