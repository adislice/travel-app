package com.uty.travelersapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.HomeCarouselWisataAdapter
import com.uty.travelersapp.databinding.FragmentHomeBinding
import com.uty.travelersapp.models.TempatWisataModel
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import com.uty.travelersapp.utils.LocationUtil
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var tempatWisataRecyclerView: RecyclerView
    private lateinit var tempatWisataArrayList: ArrayList<TempatWisataModel>

    // lokasi
    private var gpsLoc: Location? = null
    private var networkLoc: Location? = null
    private var finalLoc: Location? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var userCountry: String
    private lateinit var userAddress: String
    private val locationPermissionCode = 2

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

        binding.viewpagerHomeCarousel.apply {
            offscreenPageLimit = 3
            setPageTransformer(MarginPageTransformer(1))
            clipChildren = false  // No clipping the left and right items
            clipToPadding = false
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        tempatWisataArrayList = arrayListOf<TempatWisataModel>()

        startLocationPermissionRequest()
        val fUser = firebaseAuth.currentUser
        if (fUser != null) {
            fUser.displayName?.let {
                binding.txtDisplayName.text = it
            }
        }

        getTempatWisataData()

    }

    fun getTempatWisataData() {
        firebaseDatabase.child("objekwisata").limitToLast(4)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { tempatWisataSnapshot ->
                            val wisata =
                                tempatWisataSnapshot.getValue(TempatWisataModel::class.java)
                            tempatWisataArrayList.add(wisata!!)
                        }
                        binding.viewpagerHomeCarousel.adapter =
                            HomeCarouselWisataAdapter(tempatWisataArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("firebase", "Error : " + error.message)

//                    context?.makeToast("error: "+error.message)
                }

            })
    }

    fun getUserLocation() {

        var locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        try {
            gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
            networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (gpsLoc != null) {
            finalLoc = gpsLoc
            latitude = finalLoc!!.latitude
            longitude = finalLoc!!.longitude
        } else if (networkLoc != null) {
            finalLoc = networkLoc
            latitude = finalLoc!!.latitude
            longitude = finalLoc!!.longitude
        } else {
            latitude = 0.0
            longitude = 0.0
        }

//        requireContext().makeToast("nyampe try geocoder")


        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
                userCountry = addresses.elementAt(0).countryName
                userAddress = addresses.elementAt(0).locality
                binding.txtLocationName.text = userAddress
//                requireContext().makeToast("lokasi: "+userAddress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        if (addres != null) {
            binding.txtLocationName.text = addres.locality
        }
    }


}