package com.uty.travelersapp.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.icu.text.NumberFormat
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.android.gms.maps.model.LatLng
import com.uty.travelersapp.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random


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

    fun getLocationAddress(ctx: Context, lat: Double, lng: Double): Address? {
        val geocoder = Geocoder(ctx, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        addresses = geocoder.getFromLocation(lat, lng, 1)
        try {
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    address = addresses[0]
                    fulladdress = address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
                    return address
                } else{
                    return null
                }
            } else {
                return null
            }
        } catch (e: Exception) {
            return null
        }
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

    fun formatTanggalLengkap(inputDate: String): String {
        try {
            val inputDateString = inputDate
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

            val date: Date = inputFormat.parse(inputDateString)

            return outputFormat.format(date)
        } catch (e: Exception) {
            return ""
        }
    }

    fun formatTanggalLengkapFromDate(inputDate: Date?): String {
        if (inputDate == null) {
            return "-"
        }
        try {
            val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

            return outputFormat.format(inputDate)
        } catch (e: Exception) {
            return "-"
        }
    }

    fun dateToTanggalLengkap(inputDate: Date?): String {
        if (inputDate == null) {
            return "-"
        }
        try {
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            return outputFormat.format(inputDate)
        } catch (e: Exception) {
            return "-"
        }
    }

    fun formatRupiah(amount: Double): String? {
        try {
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            numberFormat.maximumFractionDigits = 0

            return numberFormat.format(amount)
        } catch (e: Exception) {
            return ""
        }

    }

    fun buildLoadingSpinner(ctx: Activity, title: String = "", msg: String = ""): ProgressDialog {
        val progressDialog = ProgressDialog(ctx, R.style.MyAlertDialog)
        progressDialog.apply {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setTitle(title)
            setMessage(msg)
        }
        return progressDialog
    }

    fun generateTransactionCode(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val random = Random()
        val randomCode = StringBuilder()
        repeat(10) {
            randomCode.append(random.nextInt(10))
        }

        return "INV-KNC${Calendar.getInstance().get(Calendar.YEAR)}-$randomCode"
    }

}