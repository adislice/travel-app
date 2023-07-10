package com.uty.travelersapp.extensions

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.uty.travelersapp.models.UserModel

class Helpers {
    companion object {
        fun Context.makeToast(message: CharSequence) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        fun snackbarDismiss(message: CharSequence, view: View, anchor: View? = null) {
            var sn = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            sn.setAction("OK") {
                sn.dismiss()
            }

            anchor?.let {
                sn.anchorView = anchor
            }
            sn.show()
        }

        fun calculateDistance(
            startLat: Double,
            startLong: Double,
            endLat: Double,
            endLong: Double
        ): Double {
            val locA = Location("LocationA")
            locA.apply {
                latitude = startLat
                longitude = startLong
            }
            val locB = Location("LocationB")
            locB.apply {
                latitude = endLat
                longitude = endLong
            }
            val distance: Float = locA.distanceTo(locB)
            val distanceInKm = distance / 1000
            val roundOff = Math.round(distanceInKm * 10.0) / 10.0
            return roundOff
        }

        fun Context.saveUserPref(user: UserModel) {
            val mPref = this.getSharedPreferences("MYPREF", Context.MODE_PRIVATE)
            val prefEditor = mPref.edit()
            val gson = Gson()
            val json = gson.toJson(user)
            prefEditor.putString("LOGGED_USER", json)
            prefEditor.commit()
        }

        fun Context.getUserPref(): UserModel? {
            val mPref = this.getSharedPreferences("MYPREF", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPref.getString("LOGGED_USER", "")
            val user = gson.fromJson<UserModel>(json, UserModel::class.java)
            return user
        }

        val Int.dp: Int
            get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    }
}