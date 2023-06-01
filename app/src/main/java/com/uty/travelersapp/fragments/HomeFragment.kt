package com.uty.travelersapp.fragments

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.HomeCarouselWisataAdapter
import com.uty.travelersapp.databinding.FragmentHomeBinding
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.LocationUtil
import com.uty.travelersapp.viewmodel.TempatWisataViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tempatWisataRecyclerView: RecyclerView
    private lateinit var tempatWisataArrayList: ArrayList<TempatWisataItem>
    private lateinit var tempatWisataViewModel: TempatWisataViewModel
    private lateinit var tempatWisataViewPager: ViewPager2
    private lateinit var tempatWisataCarouselAdapter: HomeCarouselWisataAdapter

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

        // Appbar customisation
        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.isAppearanceLightStatusBars = false

//        windowInsetsController?.isAppearanceLightStatusBars = false

        binding.appbarHome.addLiftOnScrollListener { elevation, backgroundColor ->
            if (binding.appbarHome.isLifted) {
                binding.toolbarHome.title = "Home"
                binding.toolbarHome.menu.findItem(R.id.menuhome_search).isVisible = true
//                topAppBar.setTitleTextColor(titleColor)
                windowInsetsController.isAppearanceLightStatusBars = true
            } else {
                binding.toolbarHome.title = " "
                binding.toolbarHome.menu.findItem(R.id.menuhome_search).isVisible = false

//                topAppBar.setTitleTextColor(Color.TRANSPARENT)
                windowInsetsController.isAppearanceLightStatusBars = false
            }
        }

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

        tempatWisataViewModel.allTempatWisata.observe(requireActivity(), Observer { twList ->
            tempatWisataArrayList.clear()
            for (item in twList) {
                tempatWisataArrayList.add(item)
            }
            tempatWisataCarouselAdapter.notifyDataSetChanged()

        })




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
        if (addres != null) {
            binding.txtLocationName.text = addres.locality
            binding.imgLocationIcon.visibility = View.VISIBLE
        }
    }


}