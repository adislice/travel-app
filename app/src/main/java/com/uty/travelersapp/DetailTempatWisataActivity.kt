package com.uty.travelersapp

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.uty.travelersapp.databinding.ActivityDetailTempatWisataBinding
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.viewmodel.TempatWisataViewModel


class DetailTempatWisataActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailTempatWisataBinding
    private var detailTempatWisata: TempatWisataItem? = null
    private lateinit var detailTwViewModel: TempatWisataViewModel
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var latLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTempatWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLayout()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        detailTempatWisata = intent.getSerializableExtra("DETAIL_TEMPATWISATA") as TempatWisataItem

        detailTempatWisata?.let {
            binding.txtDetailWisataProvinsi.text = " "
            binding.txtDetailWisataNama.text = it.nama
            binding.collapsingToolbarLayout.title = it.nama

            detailTwViewModel = ViewModelProvider(this).get(TempatWisataViewModel::class.java)
            detailTwViewModel.getDetailTempatWisata(it.id!!)
            detailTwViewModel.detailTempatWisata.observe(this, Observer { tw ->
                binding.txtDetailWisataLokasi.text = tw.alamat

                binding.txtDetailWisataDeskripsi.text = tw.deskripsi
                val imageList = ArrayList<SlideModel>()
                tw.foto?.forEach { foto ->
                    imageList.add(SlideModel(foto.url))
                }
                val imageSlider = binding.detailWisataImageSlider
                imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
                val lat = tw.latitude?.toDouble()!!
                val lng = tw.longitude?.toDouble()!!
                val lok = Helper.getAddress(this, lat, lng)
                binding.txtDetailWisataAlamat.text = lok
                latLng = LatLng(lat, lng)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                googleMap.moveCamera(cameraUpdate)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(tw.nama)
                    .snippet(tw.alamat)
                val marker = googleMap.addMarker(markerOptions)
                marker?.showInfoWindow()
            })

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    fun initializeLayout() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_detailwisata)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout_detailwisata)

        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
        var scrollRange = -1

        appBarLayout.addOnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            var koma = ( (verticalOffset * -1) / scrollRange.toDouble() )
            var percent:Double = koma * 100
            binding.tvCheck.text = percent.toInt().toString()
            if (percent <= 86) {
                windowInsetsController.isAppearanceLightStatusBars = false
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24_white)
            } else {
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
                windowInsetsController.isAppearanceLightStatusBars = true
            }
        }
    }

}