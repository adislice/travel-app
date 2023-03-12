package com.uty.travelersapp.extensions

import android.content.Context
import android.location.Location
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

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
        ): Float {
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
            return distance
        }
    }
}