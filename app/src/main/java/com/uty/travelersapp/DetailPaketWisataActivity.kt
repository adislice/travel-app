package com.uty.travelersapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.uty.travelersapp.adapters.ListTujuanWisataAdapter
import com.uty.travelersapp.databinding.ActivityDetailPaketWisataBinding
import com.uty.travelersapp.fragments.PemesananBottomSheet
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.viewmodel.PaketWisataViewModel


class DetailPaketWisataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPaketWisataBinding
    private lateinit var rvTujuanWisata: RecyclerView
    private lateinit var tujuanWisataAdapter: ListTujuanWisataAdapter
    private lateinit var pwRepository: PaketWisataRepository
    private lateinit var googleMap: GoogleMap
    private lateinit var markerList: ArrayList<Marker>
    private lateinit var mapFragment: SupportMapFragment
    private val paketWisataViewModel by viewModels<PaketWisataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPaketWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (savedInstanceState != null) {
//            mapFragment = (supportFragmentManager.findFragmentByTag("map") as SupportMapFragment?)!!
//        } else {
//            mapFragment = SupportMapFragment.newInstance()
//            val mapTransaction = supportFragmentManager.beginTransaction()
//            mapTransaction.addToBackStack("map").add(R.id.map_tujuan_wisata_1, mapFragment, "map").commit()
//        }
        initializeLayout()

//        mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map_tujuan_wisata_1) as SupportMapFragment

//        mapFragment.getMapAsync(this)
//        markerList = ArrayList()

        val detailPaketWisata = intent.getSerializableExtra("DETAIL_PAKETWISATA") as PaketWisataItem
        detailPaketWisata?.let { pw ->

            binding.txtDetailPaketNama.text = pw.nama
            binding.collapsingToolbarLayoutDetailpaket.title = pw.nama
            binding.txtDetailWisataDeskripsi.text = pw.deskripsi
            binding.imgDetailPaket.load(pw.foto?.firstOrNull()) {
                crossfade(true)
                placeholder(R.drawable.image_placeholder)
            }

            val idPaket = pw.id!!
            rvTujuanWisata = binding.rvTujuanWisata
            rvTujuanWisata.layoutManager = LinearLayoutManager(this)
            rvTujuanWisata.setHasFixedSize(true)
            tujuanWisataAdapter = ListTujuanWisataAdapter()
            rvTujuanWisata.adapter = tujuanWisataAdapter
            pwRepository = PaketWisataRepository()
//            paketWisataViewModel = ViewModelProvider(this).get(PaketWisataViewModel::class.java)
            paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)

            paketWisataViewModel.tujuanWisata.observe(this, Observer { li ->
                var sortedList = li.sortedWith(compareBy { it.order })
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


            binding.btnPesanPaketWisata.setOnClickListener {
//                val intent = Intent(this, PemesananPaketWisataActivity::class.java)
//                startActivity(intent)
//                val bttm = BottomSheetDialog(this)
//                bttm.setContentView(R.layout.modal_pemesanan_bottom_sheet)
//                bttm.show()
                val bottomSheet = PemesananBottomSheet()
                val args = Bundle()
                args.putString("PAKET_WISATA_ID", pw.id!!)
                bottomSheet.arguments = args

                bottomSheet.show(supportFragmentManager, PemesananBottomSheet.TAG)
            }

        }

//        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout_detailpaket)
//        appBarLayout.addLiftOnScrollListener { elevation, backgroundColor ->
//
//            binding.tvCheck.text = "state : " + elevation.toString()
//        }

    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        this.googleMap = googleMap
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val manager: FragmentManager = supportFragmentManager
//        val trans: FragmentTransaction = manager.beginTransaction()
//        trans.remove(mapFragment)
//        try {
//            if (trans != null && mapFragment != null) {
//                trans.commit()
//            }
//        } catch (e: Exception) {
//
//        }
//    }

    fun initializeLayout() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_detailpaket)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout_detailpaket)

        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24_white)
        var isShow = true
        var scrollRange = -1

        binding.appbarLayoutDetailpaket.addOnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }

            var koma = ( (verticalOffset * -1) / scrollRange.toDouble() )
            var percent:Double = koma * 100
            binding.tvCheck.text = "state : " + percent.toInt().toString()
            if (percent.toInt() <= 86) {
                windowInsetsController.isAppearanceLightStatusBars = false
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24_white)
                isShow = false

            } else {
                isShow = true
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
                windowInsetsController.isAppearanceLightStatusBars = true
            }
        }
    }
}