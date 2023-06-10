package com.uty.travelersapp.fragments

import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.uty.travelersapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.LocationUtil

class LocationPickerFragment : Fragment() {
    private var lokasiAnda: Location? = null
    private var lokasiTerpilih: LatLng? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val locationUtil = LocationUtil(requireContext())
        lokasiAnda = locationUtil.getUserLocation()
        Log.d("kencana", "lokasi anda: " + lokasiAnda.toString())
//        if (lokasiAnda != null) {
            val lokasiAndaLatLng = LatLng(lokasiAnda!!.latitude, lokasiAnda!!.longitude)
        lokasiTerpilih = lokasiAndaLatLng
        Log.d("kencana", "latlng: " + lokasiAndaLatLng.toString())
        val txtLokasi = view?.findViewById<TextView>(R.id.txt_lokasi_terdeteksi)

        txtLokasi?.text = Helper.getAddress(requireActivity(), lokasiAnda!!.latitude, lokasiAnda!!.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lokasiAndaLatLng, 15f)
            googleMap.moveCamera(cameraUpdate)
//        } else {
//            val sydney = LatLng(-34.0, 151.0)
////            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        }


        val oldPosition = googleMap.cameraPosition.target

        googleMap.setOnCameraMoveStartedListener {
            // drag started
        }

        googleMap.setOnCameraIdleListener {
            // get new position setelah di drag
            val newPosition = googleMap.cameraPosition.target
            if (newPosition != oldPosition) {
                // lokasi setelah selesai drag
                val locAddress = Helper.getAddress(requireActivity(), newPosition.latitude, newPosition.longitude)
                lokasiTerpilih = newPosition
                txtLokasi?.text = locAddress
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_picker) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val llBawah = view.findViewById<LinearLayout>(R.id.ll_bawah_map)
        ViewCompat.setOnApplyWindowInsetsListener(llBawah) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val mlp = view.layoutParams as ViewGroup.MarginLayoutParams
            mlp.bottomMargin = insets.bottom
            view.layoutParams = mlp

            WindowInsetsCompat.CONSUMED
        }

        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_lokasi)
        btnSimpan.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("MAP_PICKER_LAT_LNG", lokasiTerpilih)
            findNavController().popBackStack()
        }
    }
}