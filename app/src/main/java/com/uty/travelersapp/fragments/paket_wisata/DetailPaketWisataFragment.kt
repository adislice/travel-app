package com.uty.travelersapp.fragments.paket_wisata

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import com.uty.travelersapp.adapters.ListTujuanWisataAdapter
import com.uty.travelersapp.databinding.FragmentDetailPaketWisataBinding
import com.uty.travelersapp.fragments.PemesananBottomSheet
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.PermissionUtils
import com.uty.travelersapp.viewmodel.AlurPemesananViewModel
import com.uty.travelersapp.viewmodel.PaketWisataViewModel


class DetailPaketWisataFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentDetailPaketWisataBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvTujuanWisata: RecyclerView
    private lateinit var tujuanWisataAdapter: ListTujuanWisataAdapter
    private lateinit var pwRepository: PaketWisataRepository
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private val alurPemesananViewModel: AlurPemesananViewModel by activityViewModels()
    private var idPaket: String? = null
    private lateinit var googleMap: GoogleMap
    private var markerList = arrayListOf<Marker>()
    private var polylineList = arrayListOf<Polyline>()
    private var polygonList = arrayListOf<Polygon>()
    private lateinit var mapFragment: SupportMapFragment
    private var tujuanwisataList = arrayListOf<TempatWisataArrayItem>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var markerColors = arrayOf(
        Color.parseColor("#B3F44336"), // red
        Color.parseColor("#B32196F3"), // blue
        Color.parseColor("#B3009688"), // green
        Color.parseColor("#B3F9A825"), //yellow
        Color.parseColor("#B39C27B0"), //purple
        Color.parseColor("#B3E91E63"), // pink
        Color.parseColor("#B300BCD4"),
        Color.parseColor("#B3009688"), // teal/ cyan
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailPaketWisataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idTransaksi = activity?.intent?.getStringExtra(IntentKey.TRANSAKSI_ID)
        if (idTransaksi != null) {
            findNavController().popBackStack()
            val bundle = Bundle()
            bundle.putString(IntentKey.TRANSAKSI_ID, idTransaksi)
            findNavController().navigate(R.id.transaksiFragment, bundle)
            return
        }
        val id = activity?.intent?.getStringExtra("PAKET_WISATA_ID")
        mapFragment = (childFragmentManager.findFragmentById(R.id.map_tujuan_wisata_1) as SupportMapFragment?)!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionUtils.requestFineLocationPermission(requireActivity(), 29)
            return
        }
        initializeLayout()


        val detailPaketWisata = activity?.intent?.getSerializableExtra(IntentKey.DETAIL_PAKET_WISATA) as String
        detailPaketWisata.let { pwId ->
            paketWisataViewModel.setPaketWisataId(pwId)
            paketWisataViewModel.getDetailPaketWisata.observe(viewLifecycleOwner) { response ->
                when(response) {
                    is Response.Loading -> {

                    }
                    is Response.Success -> {
                        var pw = response.data
                        idPaket = pw.id
                        binding.txtDetailPaketNama.text = pw.nama
                        binding.collapsingToolbarLayoutDetailpaket.title = pw.nama
                        binding.txtDetailWisataDeskripsi.text = pw.deskripsi
                        binding.txtDetailWisataFasilitas.text = pw.fasilitas
                        binding.txtDetailWisataJamBerangkat.text = "Jam ${pw.jam_keberangkatan} dari lokasi penjemputan "

//                        binding.imgDetailPaket.load(pw.foto?.firstOrNull()) {
//                            crossfade(true)
//                            placeholder(R.drawable.image_placeholder)
//                        }
                        val imageList = ArrayList<SlideModel>()
                        pw.foto?.forEach { foto ->
                            imageList.add(SlideModel(foto))
                        }
                        binding.imgDetailPaketSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
                        binding.txtJmlTujuan.text = pw.tempat_wisata?.size?.toString() + " tempat wisata"
                        pw.waktu_perjalanan?.let {
                            var waktu = ""
                            waktu += it.hari.toString() + " hari"
                            if (!it.malam.equals(0)){
                                waktu += " " + it.malam.toString() + " malam"
                            }
                            binding.txtWaktuPerjalanan.text = waktu
                        }

                        val idPaket = pw.id!!
                        rvTujuanWisata = binding.rvTujuanWisata
                        rvTujuanWisata.layoutManager = LinearLayoutManager(requireActivity())
                        rvTujuanWisata.setHasFixedSize(true)
                        tujuanWisataAdapter = ListTujuanWisataAdapter()
                        rvTujuanWisata.adapter = tujuanWisataAdapter
                        pwRepository = PaketWisataRepository()

                        paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)
                        paketWisataViewModel.tujuanWisata.observe(viewLifecycleOwner, Observer { li ->
                            var sortedList = li.sortedWith(compareBy { it.order })
                            tujuanWisataAdapter.updateList(sortedList)
                            tujuanwisataList = ArrayList(sortedList)
                            alurPemesananViewModel.setTujuanWisata(ArrayList(sortedList))
                            val mapDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_marker_new)!!
                            for (marker in markerList) {
                                marker.remove()
                            }
                            for (poly in polygonList) {
                                poly.remove()
                            }

                            val markerPoints = arrayListOf<LatLng>()

                            for ((index, item) in tujuanwisataList.withIndex()) {
                                val latLng = LatLng(item.tempat_wisata_data!!.latitude!!.toDouble(), item.tempat_wisata_data!!.longitude!!.toDouble())
                                markerPoints.add(latLng)
                                Log.d("kencana", "adding marker " + latLng.toString())
                                val colorInt = markerColors[index]
                                val hsv = FloatArray(3)
                                Color.colorToHSV(colorInt, hsv)
                                val hue = hsv[0]
                                val alpha = Color.alpha(colorInt)
                                val iconDraw = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_marker_point)!!
                                iconDraw.setColorFilter(markerColors[index], PorterDuff.Mode.SRC_ATOP)
                                val iconRes = getMarkerIconFromDrawable(iconDraw)
                                val icon = BitmapDescriptorFactory.defaultMarker(hue)
                                val markerOptions = MarkerOptions()
                                    .position(latLng)
                                    .title(item.tempat_wisata_data?.nama)
                                    .icon(iconRes)
                                    .snippet(item.tempat_wisata_data?.kota + ", " + item.tempat_wisata_data?.provinsi)
                                val newMarker = googleMap.addMarker(markerOptions)

                                if (newMarker != null) {
                                    markerList.add(newMarker)
                                }

                            }
                            val builder = LatLngBounds.Builder()
                            for (point in markerPoints) {
                                builder.include(point)
                            }
                            val bounds = builder.build()
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
                            markerPoints.forEachIndexed { idx, lat ->
                                if (idx != markerPoints.size-1){
                                    drawCurveOnMap(markerPoints[idx], markerPoints[idx+1], 0.4)
                                }
                            }


                        })
                        mapFragment.getMapAsync(this)

                    }
                    else -> {}
                }
            }



            alurPemesananViewModel.tujuanWisata.observe(viewLifecycleOwner) {
                Log.d("kencana", "destinasi: " + it.toString())
            }

            binding.btnPesanPaketWisata.setOnClickListener {
                val bottomSheet = PemesananBottomSheet()
                val args = Bundle()
                args.putString("PAKET_WISATA_ID", idPaket)
                bottomSheet.arguments = args

                bottomSheet.show(childFragmentManager, PemesananBottomSheet.TAG)
            }

        }
    }

    fun initializeLayout() {

        val llBawah = binding.bottomBar
        ViewCompat.setOnApplyWindowInsetsListener(llBawah) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val pb = insets.bottom + 10
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                pb
            )
            binding.llKontenWrapper.setPadding(
                binding.llKontenWrapper.paddingLeft,
                binding.llKontenWrapper.paddingTop,
                binding.llKontenWrapper.paddingRight,
                pb
            )
            WindowInsetsCompat.CONSUMED
        }
        val toolbar = binding.toolbarDetailpaket
        toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24_white)

    }

    override fun onMapReady(mapz: GoogleMap?) {
        if (mapz != null) {

            this.googleMap = mapz
            googleMap.setOnCameraMoveStartedListener {reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    binding.nestedScroll.requestDisallowInterceptTouchEvent(true);

                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) {
                    binding.nestedScroll.requestDisallowInterceptTouchEvent(true);

                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_DEVELOPER_ANIMATION) {
                    binding.nestedScroll.requestDisallowInterceptTouchEvent(true);

                }
            }
            googleMap.setOnCameraIdleListener {
                binding.nestedScroll.requestDisallowInterceptTouchEvent(false);
            }


        }
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

    fun drawCurveOnMap(latLng1: LatLng, latLng2: LatLng, k: Double) {

        val polyline = googleMap.addPolyline(PolylineOptions()
            .add(latLng1, latLng2)
            .color(Color.parseColor("#99000000")))

        polylineList.add(polyline)
        return

    }

}
