package com.uty.travelersapp.fragments.paket_wisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.ListTujuanWisataAdapter
import com.uty.travelersapp.databinding.FragmentDetailPaketWisataBinding
import com.uty.travelersapp.fragments.PemesananBottomSheet
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.viewmodel.PaketWisataViewModel
import com.uty.travelersapp.viewmodel.PemesananViewModel

class DetailPaketWisataFragment : Fragment() {
    private var _binding: FragmentDetailPaketWisataBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvTujuanWisata: RecyclerView
    private lateinit var tujuanWisataAdapter: ListTujuanWisataAdapter
    private lateinit var pwRepository: PaketWisataRepository
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private val pemesananViewModel: PemesananViewModel by activityViewModels()
    private var idPaket: String? = null


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
                        Glide.with(this)
                            .load(pw.foto?.firstOrNull())
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(binding.imgDetailPaket)

                        val idPaket = pw.id!!
                        rvTujuanWisata = binding.rvTujuanWisata
                        rvTujuanWisata.layoutManager = LinearLayoutManager(requireActivity())
                        rvTujuanWisata.setHasFixedSize(true)
                        tujuanWisataAdapter = ListTujuanWisataAdapter()
                        rvTujuanWisata.adapter = tujuanWisataAdapter
                        pwRepository = PaketWisataRepository()

                        paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)
                        paketWisataViewModel.tujuanWisata.observe(requireActivity(), Observer { li ->
                            var sortedList = li.sortedWith(compareBy { it.order })
                            tujuanWisataAdapter.updateList(sortedList)
                            pemesananViewModel.setTujuanWisata(ArrayList(sortedList))
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

                    else -> {}
                }
            }



            pemesananViewModel.tujuanWisata.observe(viewLifecycleOwner) {
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

        val windowInsetsController = WindowCompat.getInsetsController(requireActivity().window, requireActivity().window.decorView)

        val toolbar = binding.toolbarDetailpaket
        toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }


        val appBarLayout = binding.appbarLayoutDetailpaket

        toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24_white)
        var isShow = true
        var scrollRange = -1

//        binding.appbarLayoutDetailpaket.addOnOffsetChangedListener { barLayout, verticalOffset ->
//            if (scrollRange == -1) {
//                scrollRange = barLayout?.totalScrollRange!!
//            }
//
//            var koma = ( (verticalOffset * -1) / scrollRange.toDouble() )
//            var percent:Double = koma * 100
//            binding.tvCheck.text = "state : " + percent.toInt().toString()
//            if (percent.toInt() <= 86) {
//                windowInsetsController.isAppearanceLightStatusBars = false
//                toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24_white)
//                isShow = false
//
//            } else {
//                isShow = true
//                toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24)
//                windowInsetsController.isAppearanceLightStatusBars = true
//            }
//        }
    }
}