package com.uty.travelersapp.fragments.paket_wisata

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bumptech.glide.Glide
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.uty.travelersapp.ProsesTransaksiActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.ListTujuanWisataAdapter
import com.uty.travelersapp.databinding.FragmentTransaksiBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.repository.PaketWisataRepository
import com.uty.travelersapp.utils.ColorStatus
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.PaketWisataViewModel
import com.uty.travelersapp.viewmodel.TransaksiViewModel


class TransaksiFragment : Fragment() {
    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private val transaksiViewModel by activityViewModels<TransaksiViewModel>()
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private var idTransaksi: String? = null
    private var idPaketWisata: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        // Inflate the layout for requireContext() fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idTransaksi = arguments?.getString(IntentKey.TRANSAKSI_ID)

        binding.btnTutup.setOnClickListener {
            requireActivity().finish()
        }


        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if (idTransaksi == null) {
            requireContext().makeToast("Id transaksi tidak ditemukan")
        }

        val loadingSpinner = Helper.buildLoadingSpinner(requireActivity(), "Loading", "Memuat transaksi...")

        if (idTransaksi != null) {
            transaksiViewModel.getDetailTransaksi(idTransaksi!!).observe(viewLifecycleOwner) { response ->
                when(response) {
                    is Response.Loading -> {
                        loadingSpinner.show()
                    }
                    is Response.Success -> {
                        loadingSpinner.dismiss()
                        response.data.paket_wisata?.let {
                            it.id?.let {
                                idPaketWisata = it
                            }
                        }

                        val namaProdukPaket = "${response.data?.paket_wisata?.nama} (${response.data?.produk?.nama})"
                        binding.txtTotalBayar.text = Helper.formatRupiah(response.data?.total_bayar!!)
                        binding.txtKodeTransaksi.text = response.data.kode_transaksi

                        when(response.data.status) {
                            Status.BELUM_BAYAR -> {
                                binding.txtStatus.text = "BELUM BAYAR"
                                binding.txtStatus.setTextColor(ColorStatus.BELUM_BAYAR_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.BELUM_BAYAR_BG)
                            }
                            Status.SELESAI -> {
                                binding.txtStatus.text = "SELESAI"
                                binding.txtStatus.setTextColor(ColorStatus.SELESAI_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.SELESAI_BG)
                                binding.btnBayarSekarang.visibility = View.GONE
                            }
                            Status.PENDING -> {
                                binding.txtStatus.text = "PENDING"
                                binding.txtStatus.setTextColor(ColorStatus.PENDING_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.PENDING_BG)
                            }
                            else -> {
                                binding.txtStatus.text = "TIDAK DIKETAHUI"
                                binding.txtStatus.setTextColor(Color.GRAY)
                            }
                        }
                        binding.txtTransaksiNamaPaket.text = namaProdukPaket
                        binding.txtTransaksiHargaPaket.text = Helper.formatRupiah(response.data.produk?.harga!!)
                        if (!response.data.promo?.kode.isNullOrEmpty()) {
                            binding.txtTransaksiDiskonNama.text = "Promo Diskon " + response.data.promo?.diskon_persen + "%"
                            binding.txtTransaksiDiskonPotongan.text = "- " + Helper.formatRupiah(response.data.promo?.potongan!!)
                        } else {
                            binding.txtTransaksiDiskonNama.visibility = View.GONE
                            binding.txtTransaksiDiskonPotongan.visibility = View.GONE
                        }
                        binding.txtTotalSemua.text = Helper.formatRupiah(response.data?.total_bayar!!)
                        binding.txtNamaPaket.text = response.data.paket_wisata?.nama
                        binding.txtNamaProduk.text = response.data.produk.nama
                        binding.txtHargaProduk.text = Helper.formatRupiah(response.data.produk.harga)
                        binding.txtTglKeberangkatan.text = Helper.formatTanggalLengkapFromDate(response.data.keberangkatan?.tanggal_perjalanan)
                        binding.txtNotelpPemesan.text = response.data.user?.no_telp
                        val lokPenjemputanLat = response.data.keberangkatan?.lokasi_penjemputan_lat?.toDoubleOrNull()
                        val lokPenjemputanLong = response.data.keberangkatan?.lokasi_penjemputan_long?.toDoubleOrNull()
                        if (lokPenjemputanLat != null && lokPenjemputanLong != null) {
                            val address = Helper.getAddress(requireActivity(), lokPenjemputanLat, lokPenjemputanLong)
                            binding.txtAlamatJemput.text = address
                        }

                        // payment api

                        binding.btnBayarSekarang.setOnClickListener {
                            val intent = Intent(requireActivity(), ProsesTransaksiActivity::class.java)
                            intent.putExtra(IntentKey.TRANSAKSI_ID, idTransaksi)
                            startActivity(intent)
                        }

                        // list tujuan wisata
                        val llTujuanWisata = binding.llTujuanWisata
                        paketWisataViewModel.setPaketWisataId(idPaketWisata)
                        paketWisataViewModel.getDetailPaketWisata.observe(viewLifecycleOwner) { response ->
                            when(response) {
                                is Response.Loading -> {

                                }
                                is Response.Success -> {
                                    var pw = response.data
                                    paketWisataViewModel.getTujuanWisata(pw.tempat_wisata!!)
                                    paketWisataViewModel.tujuanWisata.observe(viewLifecycleOwner) { li ->
                                        llTujuanWisata.removeAllViews()
                                        var sortedList = li.sortedWith(compareBy { it.order })
                                        sortedList.forEachIndexed { index, destinasi ->
                                            val infl = layoutInflater.inflate(R.layout.item_timeline_tempatwisata_besar, null, false)
                                            infl.findViewById<TextView>(R.id.txt_tl_nama_tempat_wisata).text = destinasi.tempat_wisata_data?.nama
//                                            infl.findViewById<TextView>(R.id.txt_tl_order).text = "TUJUAN " + (index + 1).toString()
                                            infl.findViewById<TextView>(R.id.txt_tl_alamat).text = "TUJUAN " + (index + 1).toString() + " • " + "${destinasi.tempat_wisata_data?.kota}, ${destinasi.tempat_wisata_data?.provinsi}"
                                            infl.findViewById<ImageView>(R.id.img_tl_tujuan_gambar).load(destinasi.tempat_wisata_data?.foto?.firstOrNull()) {
                                                placeholder(R.drawable.loading_image_placeholder)
                                                crossfade(true)
                                            }
                                            if (index == 0) {
                                                infl.findViewById<View>(R.id.tl_garis_atas).visibility = View.INVISIBLE
                                            }
                                            if (index == sortedList.size - 1) {
                                                infl.findViewById<View>(R.id.tl_garis_bawah).visibility = View.INVISIBLE
                                            }
                                            llTujuanWisata.addView(infl)
                                        }
                                    }
                                }

                                else -> {}
                            }
                        }



                    }
                    is Response.Failure -> {
                        requireContext().makeToast("Error mengambil transaksi: " + response.errorMessage)
                    }
                }
            }
        }
    }

    private fun buildUiKit() {
        SdkUIFlowBuilder.init().apply {
            setClientKey("SB-Mid-client-9ISWkdD18OTVrVhh")
            setContext(requireActivity())
            setTransactionFinishedCallback { result ->
                Toast.makeText(requireContext(), "Transaction Result: " + result.status, Toast.LENGTH_LONG).show()

            }
            setMerchantBaseUrl("https://kencana-admin.vercel.app/api/")
            setColorTheme(CustomColorTheme("#" + Integer.toHexString(ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)), "#B61548", "#FFE51255"))
        }.buildSDK()

    }

}