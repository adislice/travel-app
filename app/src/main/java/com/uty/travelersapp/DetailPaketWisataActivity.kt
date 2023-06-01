package com.uty.travelersapp

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.uty.travelersapp.adapters.ListTujuanWisataAdapter
import com.uty.travelersapp.databinding.ActivityDetailPaketWisataBinding
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.viewmodel.PaketWisataViewModel


class DetailPaketWisataActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailPaketWisataBinding
    private lateinit var rvTujuanWisata: RecyclerView
    private lateinit var tujuanWisataAdapter: ListTujuanWisataAdapter
    private lateinit var pwRepository: PaketWisataRepository
    private lateinit var paketWisataViewModel: PaketWisataViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var markerList: ArrayList<Marker>
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPaketWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            mapFragment = (supportFragmentManager.findFragmentByTag("map") as SupportMapFragment?)!!
        } else {
            mapFragment = SupportMapFragment.newInstance()
            val mapTransaction = supportFragmentManager.beginTransaction()
            mapTransaction.addToBackStack("map").add(R.id.map_tujuan_wisata_1, mapFragment, "map").commit()
        }
        initializeLayout()

//        mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map_tujuan_wisata) as SupportMapFragment

        mapFragment.getMapAsync(this)
        markerList = ArrayList()

        val detailPaketWisata = intent.getSerializableExtra("DETAIL_PAKETWISATA") as PaketWisataItem
        detailPaketWisata?.let { pw ->
            binding.btnPesanPaketWisata.setOnClickListener {
                val intent = Intent(this, PemesananPaketWisataActivity::class.java)
                startActivity(intent)
            }
            binding.txtDetailPaketNama.text = pw.nama
            binding.collapsingToolbarLayoutDetailpaket.title = pw.nama
            binding.txtDetailWisataDeskripsi.text = pw.deskripsi
            Glide.with(this)
                .load(pw.thumbnail_foto)
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.imgDetailPaket)

            val idPaket = pw.id!!
            rvTujuanWisata = binding.rvTujuanWisata
            rvTujuanWisata.layoutManager = LinearLayoutManager(this)
            rvTujuanWisata.setHasFixedSize(true)
            tujuanWisataAdapter = ListTujuanWisataAdapter()
            rvTujuanWisata.adapter = tujuanWisataAdapter
            pwRepository = PaketWisataRepository()
            paketWisataViewModel = ViewModelProvider(this).get(PaketWisataViewModel::class.java)
            paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)

            paketWisataViewModel.tujuanWisata.observe(this, Observer { li ->
                var sortedList = li.sortedWith(compareBy({it.order}))
                tujuanWisataAdapter.updateList(sortedList)
//
//                for (marker in markerList) {
//                    marker.remove()
//                }
//                for (item in li) {
//                    val latLng = LatLng(item.tempat_wisata_data!!.latitude!!.toDouble(), item.tempat_wisata_data!!.longitude!!.toDouble())
//                    val markerOptions = MarkerOptions()
//                        .position(latLng)
//                        .title(item.nama)
//                        .snippet(item.tempat_wisata_data?.alamat)
//                    val newMarker = googleMap.addMarker(markerOptions)
//                    if (newMarker != null) {
//                        markerList.add(newMarker)
//                    }
//                }
//                if (markerList.size > 0) {
//                    var midPoint = Helper.calculateMidpoint(markerList.first().position, markerList.last().position)
//                    Log.d("firebase", "midPoint : " + midPoint.toString())
//                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(midPoint, 10f)
//                    googleMap.moveCamera(cameraUpdate)
//
//                }
            })

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onDestroy() {
        super.onDestroy()
        val manager: FragmentManager = supportFragmentManager
        val trans: FragmentTransaction = manager.beginTransaction()
        trans.remove(mapFragment)
        try {
            if (trans != null && mapFragment != null) {
                trans.commit()
            }
        } catch (e: Exception) {

        }
    }

    fun initializeLayout() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_detailpaket)
        toolbar.setNavigationOnClickListener {
            finish()
        }
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var typedVal = TypedValue()
        this.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant, typedVal, true
        )
        val col = typedVal.data
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout_detailpaket)
        val collapsedColor = ContextCompat.getDrawable(
            applicationContext, com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        collapsedColor?.setColorFilter(col, PorterDuff.Mode.SRC_ATOP)
        val expandedColor = ContextCompat.getDrawable(
            applicationContext, com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        expandedColor?.setColorFilter(
            ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon = expandedColor
        val expandedTitle = ""
        var isShow = true
        var scrollRange = -1

        appBarLayout.addOnOffsetChangedListener { barLayout, verticalOffset ->
//            val colo = toolbar.background as Drawable
//            txtCount.text = (colo as ColorDrawable).color.toString()
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset <= 90) {
                isShow = true
                toolbar.navigationIcon = collapsedColor
                windowInsetsController.isAppearanceLightStatusBars = true
            } else if (isShow) {
                windowInsetsController.isAppearanceLightStatusBars = false
                toolbar.navigationIcon = expandedColor
                isShow = false
            }
        }
    }
}