package com.uty.travelersapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.switchMap
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uty.travelersapp.PaketWisataBaseActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.HomeCarouselWisataAdapter
import com.uty.travelersapp.databinding.FragmentHomeBinding
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.LocationUtil
import com.uty.travelersapp.utils.PermissionUtils
import com.uty.travelersapp.viewmodel.PaketWisataViewModel
import com.uty.travelersapp.viewmodel.TempatWisataViewModel
import com.uty.travelersapp.viewmodel.UserViewModel
import kotlinx.coroutines.delay

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tempatWisataRecyclerView: RecyclerView
    private lateinit var tempatWisataArrayList: ArrayList<TempatWisataItem>
    private lateinit var tempatWisataViewModel: TempatWisataViewModel
    private lateinit var tempatWisataViewPager: ViewPager2
    private lateinit var tempatWisataCarouselAdapter: HomeCarouselWisataAdapter
    private val userViewModel by activityViewModels<UserViewModel>()

    // lokasi
    private var gpsLoc: Location? = null
    private var networkLoc: Location? = null
    private var finalLoc: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var userCountry: String
    private lateinit var userAddress: String
    private val locationPermissionCode = 2
    private var doubleBackExitPressedOnce = false
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val isLifted = MutableLiveData<Boolean>(false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarHome.inflateMenu(R.menu.menu_home)
        binding.toolbarHome.menu.findItem(R.id.menuhome_search).isVisible = false

        binding.appbarHome.isLiftOnScroll = false
        binding.appbarHome.isLifted = true
        binding.appbarHome.alpha = 0.0f



        binding.scrollContent.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            var woi = scrollY / 500f

            isLifted.value = woi >= 1
//            binding.appbarHome.alpha = when {
//                newKoma <= 0 -> 0.0f
//                newKoma >= 1.0f -> 1.0f
//                else -> newKoma
//            }
        }

        isLifted.distinctUntilChanged().observe(viewLifecycleOwner) {
            if (it) {
                binding.appbarHome.animate().alpha(1.0f)
            } else {
                binding.appbarHome.animate().alpha(0.0f)
            }
        }

        initUser()

        binding.btnLihatsemuaTempatwisata.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNav.selectedItemId = R.id.navitem_tempat_wisata
        }

        tempatWisataViewPager = binding.viewpagerHomeCarousel
        tempatWisataViewPager.apply {
            offscreenPageLimit = 3
            setPageTransformer(MarginPageTransformer(1))
            clipChildren = false  // No clipping the left and right items
            clipToPadding = false
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        tempatWisataArrayList = arrayListOf()
        tempatWisataCarouselAdapter = HomeCarouselWisataAdapter(tempatWisataArrayList)
        tempatWisataViewPager.adapter = tempatWisataCarouselAdapter

        startLocationPermissionRequest()
        val fUser = firebaseAuth.currentUser
        if (fUser != null) {
            fUser.displayName?.let {
                binding.txtDisplayName.text = it
            }
        }

        tempatWisataViewModel = ViewModelProvider(requireActivity()).get(TempatWisataViewModel::class.java)

        tempatWisataViewModel.tempatWisataHome.observe(requireActivity(), Observer { twList ->
            tempatWisataArrayList.clear()
            for (item in twList) {
                tempatWisataArrayList.add(item)
            }
            tempatWisataCarouselAdapter.notifyDataSetChanged()

        })

        tempatWisataViewPager.autoScroll(viewLifecycleOwner.lifecycleScope, HomeCarouselWisataAdapter.REFRESH_RATE_SECONDS)

        // paket wisata item
//        homePaketAdapter = HomePaketWisataAdapter(requireContext())
//        val lvPaket = binding.lvHomePaketwisata
//        lvPaket.adapter = homePaketAdapter

        val llPaketWisata = binding.llHomePaketwisata

        paketWisataViewModel.homePaketWisata.observe(viewLifecycleOwner) { response ->
            llPaketWisata.removeAllViews()
            llPaketWisata.invalidate()

            response.forEach { item ->
                val itemView = View.inflate(requireActivity(), R.layout.item_home_paketwisata, null)
                itemView.setOnClickListener {
                    val intent = Intent(requireActivity(), PaketWisataBaseActivity::class.java)
                    intent.putExtra(IntentKey.DETAIL_PAKET_WISATA, item.id)
                    requireActivity().startActivity(intent)
                }
                itemView.findViewById<TextView>(R.id.txt_nama_paketwisata).text = item.nama
                var destinasi = ""
                val listProv = item.tempat_wisata_data?.map { it.provinsi }
                val listProvNew = listProv?.distinct()
                itemView.findViewById<TextView>(R.id.txt_destinasi).text = item.tempat_wisata_data?.size.toString() + " tempat wisata tujuan"
                var harga = ""
                itemView.findViewById<TextView>(R.id.txt_harga).text = "Mulai dari -"
                item.produk?.let {
                    var produkList = item.produk!!.sortedBy { it.harga }
                    val hargaStr = produkList.first().harga?.let { it1 -> Helper.formatRupiah(it1) }
                    var hargaJmlSeat = produkList.first().jenis_kendaraan?.jumlah_seat.toString() + " seat"
                    var hargaPerJmlSeat = "$hargaStr / $hargaJmlSeat"
                    itemView.findViewById<TextView>(R.id.txt_harga).text = "Mulai dari ${hargaPerJmlSeat}"
                }
                item.waktu_perjalanan?.let {
                    var waktu = ""
                    waktu += it.hari.toString() + " hari"
                    if (!it.malam.equals(0)){
                        waktu += " " + it.malam.toString() + " malam"
                    }
                    itemView.findViewById<TextView>(R.id.txt_waktu).text = waktu
                }

                val imgView = itemView.findViewById<ImageView>(R.id.img_thumbnail)
                imgView.load(item.foto?.firstOrNull()) {
                    placeholder(R.drawable.loading_image_placeholder)
                    crossfade(true)
                }

                llPaketWisata.addView(itemView)
            }
        }

        binding.btnLihatsemuaPaketwisata.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNav.selectedItemId = R.id.navitem_paket_wisata
        }

    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            changeDisplayAddress()
        }
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun changeDisplayAddress() {
        val locationUtil = LocationUtil(requireContext())
        val addres = locationUtil.getUserAddress()

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
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val locAddress = Helper.getLocationAddress(requireActivity(), location.latitude, location.longitude)
            locAddress?.let {addres ->
                binding.txtLocationName.text = addres.locality
                binding.imgLocationIcon.visibility = View.VISIBLE
            }

        }

    }

    fun ViewPager2.autoScroll(lifecycleScope: LifecycleCoroutineScope, interval: Long) {
        lifecycleScope.launchWhenResumed {
            scrollIndefinitely(interval)
        }

    }

    private suspend fun ViewPager2.scrollIndefinitely(interval: Long) {
        delay(interval)
        val numberOfItems = tempatWisataCarouselAdapter.itemCount ?: 0
        val lastIndex = if (numberOfItems > 0) numberOfItems - 1 else 0
        val nextItem = if (currentItem == lastIndex) 0 else currentItem + 1

        setCurrentItem(nextItem, true)
        scrollIndefinitely(interval)
    }

    fun initUser() {
        binding.imgHomeProfilePicture.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNav.selectedItemId = R.id.navitem_profil
        }
        val user = firebaseAuth.currentUser
        user?.let { currUser ->
            userViewModel.setUser(user.uid)
            userViewModel.userDetailData.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                        binding.txtDisplayName.text = response.data.nama
                        response.data.profile_picture?.let { pp ->
                           binding.imgHomeProfilePicture.load(pp) {
                               crossfade(true)
                           }
                            binding.imgHomeProfilePicture.colorFilter = null
                            binding.imgHomeProfilePicture.imageTintList = null

                        }
                    }
                    is Response.Failure -> {
                        Log.d("kencana", response.errorMessage)
                    }

                    else -> {}
                }
            }

        }
    }


}