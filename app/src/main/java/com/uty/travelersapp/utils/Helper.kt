package com.uty.travelersapp.utils

import android.content.ContentResolver
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.util.Locale


object Helper {
    fun getAddress(ctx: Context, lat: Double, lng: Double): String {
        val geocoder = Geocoder(ctx, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        addresses = geocoder.getFromLocation(lat, lng, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                address = addresses[0]
                fulladdress = address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
                var city = address.getLocality();
                var state = address.getAdminArea();
                var country = address.getCountryName();
                var postalCode = address.getPostalCode();
                var knownName = address.getFeatureName(); // Only if available else return NULL
            } else{
                fulladdress = "Location not found"
            }
        } else {
            fulladdress = "Lokasi tidak ditemukan"
        }
        return fulladdress

    }

    fun calculateMidpoint(latLng1: LatLng, latLng2: LatLng): LatLng {
        val latMid = (latLng1.latitude + latLng2.latitude ) / 2
        val longMid = (latLng1.longitude + latLng2.longitude) / 2

        return LatLng(latMid, longMid)
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?

        //Check uri format to avoid null
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }


}