package com.uty.travelersapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import java.lang.Exception
import java.util.Locale

class LocationUtil(private val context: Context) {
    private var gpsLoc: Location? = null
    private var networkLoc: Location? = null
    private var finalLoc: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var userCountry: String
    private lateinit var userAddress: String
    private val locationPermissionCode = 2

    private fun getUserLocation(): Location? {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        try {
            gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
            networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
        } catch (e: Exception){
            e.printStackTrace()
        }

        if (gpsLoc != null){
            finalLoc = gpsLoc
            latitude = finalLoc!!.latitude
            longitude = finalLoc!!.longitude
        } else if (networkLoc != null){
            finalLoc = networkLoc
            latitude = finalLoc!!.latitude
            longitude = finalLoc!!.longitude
        } else {
            finalLoc = null
        }
        MyLocation.location = finalLoc
        return finalLoc
    }

    fun getUserAddress(): Address? {
        if (finalLoc == null) {
            getUserLocation()
        }
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0){
                val address = addresses.first()
                MyLocation.address = address
                return address
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    companion object {
        fun getCityName(context: Context, long: Double, lat: Double): String {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, long, 1)
                if (addresses != null && addresses.size > 0){
                    val address = addresses.first()
                    return address.adminArea
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
            return ""
        }
    }

}