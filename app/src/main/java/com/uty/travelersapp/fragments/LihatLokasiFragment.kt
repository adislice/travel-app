package com.uty.travelersapp.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentLihatLokasiBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.DaftarLokasi
import com.uty.travelersapp.viewmodel.AlurPemesananViewModel


class LihatLokasiFragment : Fragment() {
    private var _binding: FragmentLihatLokasiBinding? = null
    private val binding get() = _binding!!
    private lateinit var gMap: GoogleMap
    private var lokasiList: ArrayList<DaftarLokasi>? = null
    private var markerList = arrayListOf<Marker>()
    private var polylineList = arrayListOf<Polyline>()
    private var markerColors = arrayOf(
        Color.parseColor("#B3424242"),
        Color.parseColor("#B3F44336"), // red
        Color.parseColor("#B32196F3"), // blue
        Color.parseColor("#B3009688"), // green
        Color.parseColor("#B3F9A825"), //yellow
        Color.parseColor("#B39C27B0"), //purple
        Color.parseColor("#B3E91E63"), // pink
        Color.parseColor("#B300BCD4"),
        Color.parseColor("#B3009688"), // teal/ cyan
    )

    private val alurPemesananViewModel by activityViewModels<AlurPemesananViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLihatLokasiBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarLocationPicker.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbarLocationPicker.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_lihat_lokasi) as SupportMapFragment?

        alurPemesananViewModel.lokasiList.observe(viewLifecycleOwner) {
//            requireActivity().makeToast("lok: " + it.toString())
            lokasiList?.clear()
            lokasiList = it
            mapFragment?.getMapAsync(callback)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.llBawahMap) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val mlp = view.layoutParams as ViewGroup.MarginLayoutParams
            mlp.bottomMargin = insets.bottom
            view.layoutParams = mlp

            WindowInsetsCompat.CONSUMED
        }

    }

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap

        val markerPoints = arrayListOf<LatLng>()
        lokasiList?.let { lokList ->
            Log.d("kencana", "loklist: " + lokList.toString())
            for ((index, lokasi) in lokList.withIndex()) {
                markerPoints.add(lokasi.lokasi)
                var iconDraw = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_marker_point)!!
                if (index == 0) {
                    iconDraw = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_marker_user)!!
                }
                if (index == lokList.size-1) {
                    iconDraw = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_marker_check)!!
                }
                iconDraw.setColorFilter(markerColors[index], PorterDuff.Mode.SRC_ATOP)
                val iconRes = getMarkerIconFromDrawable(iconDraw)

                val markerOptions = MarkerOptions()
                    .position(lokasi.lokasi)
                    .title(lokasi.nama)
                    .icon(iconRes)
                val newMarker = gMap.addMarker(markerOptions)
                if (newMarker != null) {
                    markerList.add(newMarker)
                }
            }
            val builder = LatLngBounds.Builder()
            for (point in markerPoints) {
                builder.include(point)
            }
            val bounds = builder.build()
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            markerPoints.forEachIndexed { idx, lat ->
                if (idx != markerPoints.size-1){
                    drawCurveOnMap(markerPoints[idx], markerPoints[idx+1], 0.4)
                }
            }
        }


    }


    fun drawCurveOnMap(latLng1: LatLng, latLng2: LatLng, k: Double) {
        val polyline = gMap.addPolyline(
            PolylineOptions()
            .add(latLng1, latLng2)
            .color(Color.parseColor("#99000000")))

        polylineList.add(polyline)
        return
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}