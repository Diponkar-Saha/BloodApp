package com.example.splash.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable



@Parcelize
data class Address(val street : String? = "",
                   val neighborhood : String? = "",
                   val city : String? = "",
                   val cep : String? = "",
                   val latitude : Double? = 0.0,
                   val longitude : Double? = 0.0

) : Parcelable {
}