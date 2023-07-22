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
    private var polygonList = arrayListOf<Polygon>()
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_lihat_lokasi) as SupportMapFragment?

        alurPemesananViewModel.lokasiList.observe(viewLifecycleOwner) {
            requireActivity().makeToast("lok: " + it.toString())
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

        //curve radius
        var colorInt = Color.parseColor("#1E88E5")
        var h = SphericalUtil.computeHeading(latLng1, latLng2)
        var d = 0.0
        val p: LatLng?

        //The if..else block is for swapping the heading, offset and distance
        //to draw curve always in the upward direction
        if (h < 0) {
            d = SphericalUtil.computeDistanceBetween(latLng2, latLng1)
            h = SphericalUtil.computeHeading(latLng2, latLng1)
            //Midpoint position
            p = SphericalUtil.computeOffset(latLng2, d * 0.5, h)
        } else {
            d = SphericalUtil.computeDistanceBetween(latLng1, latLng2)

            //Midpoint position
            p = SphericalUtil.computeOffset(latLng1, d * 0.5, h)
        }

        //Apply some mathematics to calculate position of the circle center
        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)

        val c = SphericalUtil.computeOffset(p, x, h + 90.0)

        //Calculate heading between circle center and two points
        val h1 = SphericalUtil.computeHeading(c, latLng1)
        val h2 = SphericalUtil.computeHeading(c, latLng2)

        //Calculate positions of points on circle border and add them to polyline options
        val numberOfPoints = 1000 //more numberOfPoints more smooth curve you will get
        val step = (h2 - h1) / numberOfPoints

        //Create PolygonOptions object to draw on map
        val polygon = PolygonOptions()

        //Create a temporary list of LatLng to store the points that's being drawn on map for curve
        val temp = arrayListOf<LatLng>()

        //iterate the numberOfPoints and add the LatLng to PolygonOptions to draw curve
        //and save in temp list to add again reversely in PolygonOptions
        for (i in 0 until numberOfPoints) {
            val latlng = SphericalUtil.computeOffset(c, r, h1 + i * step)
            polygon.add(latlng) //Adding in PolygonOptions
            temp.add(latlng)    //Storing in temp list to add again in reverse order
        }

        //iterate the temp list in reverse order and add in PolygonOptions
        for (i in (temp.size - 1) downTo 1) {
            polygon.add(temp[i])
        }

        polygon.strokeColor(colorInt)
        polygon.strokeWidth(12f)
//        polygon.strokePattern(listOf(Dash(30f), Gap(50f))) //Skip if you want solid line
        val result = gMap.addPolygon(polygon)
        polygonList.add(result)

        temp.clear() //clear the temp list
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