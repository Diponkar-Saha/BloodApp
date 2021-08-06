package com.example.splash

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.splash.databinding.FragmentPickMapsBinding
import com.example.splash.model.Address
import com.example.splash.ui.util.LocationPermission
import com.example.splash.utilites.Constants
import com.example.splash.utilites.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*

class PickMapsFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentPickMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private val location = Location()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPickMapsBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        requestPermission()
    }

    private fun requestPermission(){
        if(LocationPermission.hasLocationPermission(requireContext())){
            return }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(this,
                getString(R.string.needs_your_permission),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            EasyPermissions.requestPermissions(this,
                getString(R.string.needs_your_permission),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
            activity?.let { location.getCurrentLocation(mMap, it.applicationContext) }
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener { latLng ->
            showDialogChoiceAddress(latLng)
        }
    }

    private fun showDialogChoiceAddress(latLng: LatLng) {
        val alert = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_confirm_address,null)
        alert.setView(view)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val textViewAddress : TextView = view!!.findViewById(R.id.textViewAddressDialog)
        val textViewNo: TextView = view.findViewById(R.id.textViewNoDialog)
        val buttonConfirm : Button = view.findViewById(R.id.buttonConfirmDialogAddress)

        textViewAddress.text = latLng.toString()

        val geocoder = Geocoder(activity, Locale.getDefault())
        try{
            val addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
            val addressNotFormatted = addressList[0].getAddressLine(0)
            Log.i(TAG, "confirmAddressDialog: AddressNotFormateed: $addressNotFormatted")
            val splitAddress = addressNotFormatted.split(",")


            Log.i(TAG, "confirmAddressDialog: ${addressList}")

            val address = Address(splitAddress[0], splitAddress[1], splitAddress[2],
                splitAddress[4], latLng.latitude, latLng.longitude
            )

            textViewAddress.text = "$address.street - $address.neighborhood"
            textViewNo.setOnClickListener {
                dialog.cancel()
            }
            buttonConfirm.setOnClickListener {
                val addressBundle = Bundle().apply {
                    putParcelable("latLngAddress",address)

                }
                findNavController().navigate(R.id.action_pickMapsFragment_to_oppoFragment,addressBundle)
                dialog.cancel()
            }

        }catch (e : IOException){
            Log.i(TAG, "confirmAddressDialog: Erro ao obter o endereco: ${e.message} ")

            dialog.cancel()
            buttonConfirm.isEnabled = false
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermission()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)
        findNavController().navigate(R.id.action_pickMapsFragment_to_blankFragment)
    }


}