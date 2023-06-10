package com.uty.travelersapp.fragments.paket_wisata

import android.content.Intent
import android.os.Bundle
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
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.viewmodel.PaketWisataViewModel

class DetailPaketWisataFragment : Fragment() {
    private var _binding: FragmentDetailPaketWisataBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvTujuanWisata: RecyclerView
    private lateinit var tujuanWisataAdapter: ListTujuanWisataAdapter
    private lateinit var pwRepository: PaketWisataRepository
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()


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
        val id = activity?.intent?.getStringExtra("PAKET_WISATA_ID")
//        val txtCoba = view.findViewById<TextView>(R.id.txt_test_coba)
//        txtCoba.text = id
//        val btn = view.findViewById<Button>(R.id.btn_ke_dua)
//        btn.setOnClickListener {
//            findNavController().navigate(R.id.action_detailPaketWisataFragment_to_pesanPaketWisataFragment)
//        }

        initializeLayout()

        val llBawah = binding.bottomBar
        ViewCompat.setOnApplyWindowInsetsListener(llBawah) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val mlp = view.layoutParams as MarginLayoutParams
            mlp.bottomMargin = insets.bottom
            view.layoutParams = mlp

            WindowInsetsCompat.CONSUMED
        }

        val detailPaketWisata = activity?.intent?.getSerializableExtra("DETAIL_PAKETWISATA") as PaketWisataItem
        detailPaketWisata?.let { pw ->

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
            rvTujuanWisata.layoutManager = LinearLayoutManager(requireActivity())
            rvTujuanWisata.setHasFixedSize(true)
            tujuanWisataAdapter = ListTujuanWisataAdapter()
            rvTujuanWisata.adapter = tujuanWisataAdapter
            pwRepository = PaketWisataRepository()
//            paketWisataViewModel = ViewModelProvider(this).get(PaketWisataViewModel::class.java)
            paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)

            paketWisataViewModel.tujuanWisata.observe(requireActivity(), Observer { li ->
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

                bottomSheet.show(childFragmentManager, PemesananBottomSheet.TAG)
//                findNavController().navigate(R.id.action_detailPaketWisataFragment_to_pemesananBottomSheet, args)
            }

        }
    }

    fun initializeLayout() {
        val windowInsetsController = WindowCompat.getInsetsController(requireActivity().window, requireActivity().window.decorView)

        val toolbar = binding.toolbarDetailpaket
        toolbar?.setNavigationOnClickListener {
//            finish()
        }

        val appBarLayout = binding.appbarLayoutDetailpaket

        toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24_white)
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
                toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24_white)
                isShow = false

            } else {
                isShow = true
                toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24)
                windowInsetsController.isAppearanceLightStatusBars = true
            }
        }
    }
}