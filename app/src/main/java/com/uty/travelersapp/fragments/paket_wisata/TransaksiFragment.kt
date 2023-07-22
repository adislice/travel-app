package com.uty.travelersapp.fragments.paket_wisata

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.uty.travelersapp.ProsesTransaksiActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentTransaksiBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.DaftarLokasi
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.utils.ColorStatus
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.viewmodel.AlurPemesananViewModel
import com.uty.travelersapp.viewmodel.PaketWisataViewModel
import com.uty.travelersapp.viewmodel.PemesananViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class TransaksiFragment : Fragment() {
    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private val pemesananViewModel by activityViewModels<PemesananViewModel>()
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private val alurPemesananViewModel by activityViewModels<AlurPemesananViewModel>()
    private var idTransaksi: String? = null
    private var idPaketWisata: String = ""
    private var lokasiList = arrayListOf<DaftarLokasi>()
    private lateinit var lokasiJemput: DaftarLokasi
    private var fileName = "kencana"

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
        Log.d("kencana", "Fragment transaksi dibuka, id: " + idTransaksi)

        binding.btnTutup.setOnClickListener {
            requireActivity().finish()
        }


        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        if (idTransaksi == null) {
            requireContext().makeToast("Id transaksi tidak ditemukan")
        }

        val loadingSpinner = Helper.buildLoadingSpinner(requireActivity(), "Loading", "Memuat transaksi...")

        if (idTransaksi != null) {
            pemesananViewModel.getDetailPemesanan(idTransaksi!!).observe(viewLifecycleOwner) { response ->
                when(response) {
                    is Response.Loading -> {
                        loadingSpinner.show()
                    }
                    is Response.Success -> {
                        lokasiList.clear()
                        loadingSpinner.dismiss()
                        response.data.paket_wisata?.let {
                            it.id?.let {
                                idPaketWisata = it
                            }
                        }

                        val namaProdukPaket = "${response.data?.paket_wisata?.nama} (${response.data?.jenis_kendaraan?.nama})"
                        binding.txtTotalBayar.text = Helper.formatRupiah(response.data?.total_bayar!!)
                        binding.txtKodeTransaksi.text = response.data.kode_pemesanan
                        fileName = response.data.kode_pemesanan!!

                        when(response.data.status) {
                            Status.DIPROSES -> {
                                binding.txtStatus.text = "DIPROSES"
                                binding.txtStatus.setTextColor(ColorStatus.BELUM_BAYAR_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.BELUM_BAYAR_BG)
                                binding.labelBatasAtauTglBayar.text = "Tanggal Bayar"
                                binding.txtBatasAtauTglBayar.text = "-"
                            }
                            Status.SELESAI -> {
                                binding.txtStatus.text = "SELESAI"
                                binding.txtStatus.setTextColor(ColorStatus.SELESAI_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.SELESAI_BG)
                                binding.btnBayarSekarang.visibility = View.GONE
                                binding.btnUnduhBukti.visibility = View.VISIBLE
                                val successIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.task_alt_fill0_wght600_grad0_opsz48)
                                binding.imgStatusIcon.setImageDrawable(successIcon)
                                binding.imgStatusIcon.setColorFilter(Color.parseColor("#388E3C"))
                                binding.imgStatusIcon.backgroundTintList = ColorStateList.valueOf(ColorStatus.SELESAI_BG)
                                response.data.pembayaran?.tanggal_bayar?.let {
                                    binding.labelBatasAtauTglBayar.text = "Tanggal Bayar"
                                    binding.txtBatasAtauTglBayar.text = Helper.formatTanggalDanWaktu(it)
                                }
                            }
                            Status.PENDING -> {
                                binding.txtStatus.text = "PENDING"
                                binding.txtStatus.setTextColor(ColorStatus.PENDING_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.PENDING_BG)
                                response.data.pembayaran?.batas_bayar?.let {
                                    binding.labelBatasAtauTglBayar.text = "Bayar Sebelum"
                                    binding.txtBatasAtauTglBayar.text = Helper.formatTanggalDanWaktu(it)
                                }
                            }
                            Status.DIBATALKAN -> {
                                binding.txtStatus.text = "DIBATALKAN"
                                binding.txtStatus.setTextColor(ColorStatus.DIBATALKAN_TEXT)
                                binding.txtStatus.backgroundTintList = ColorStateList.valueOf(ColorStatus.DIBATALKAN_BG)
                                binding.labelBatasAtauTglBayar.text = "Tanggal Bayar"
                                binding.txtBatasAtauTglBayar.text = ""
                            }
                            else -> {
                                binding.txtStatus.text = "TIDAK DIKETAHUI"
                                binding.txtStatus.setTextColor(Color.GRAY)
                            }
                        }
                        binding.txtTransaksiNamaPaket.text = namaProdukPaket
                        binding.txtTransaksiHargaPaket.text = Helper.formatRupiah(response.data.produk?.harga!!)
                        if (!response.data.promo?.kode.isNullOrEmpty()) {
                            binding.txtTransaksiDiskonNama.text = "Promo Diskon " + response.data.promo?.persen + "%"
                            binding.txtTransaksiDiskonPotongan.text = "- " + Helper.formatRupiah(response.data.promo?.potongan!!)
                        } else {
                            binding.txtTransaksiDiskonNama.visibility = View.GONE
                            binding.txtTransaksiDiskonPotongan.visibility = View.GONE
                        }
                        binding.txtTotalSemua.text = Helper.formatRupiah(response.data?.total_bayar!!)
                        binding.txtNamaPaket.text = response.data.paket_wisata?.nama
                        binding.txtNamaProduk.text = "${response.data.jenis_kendaraan?.nama} (${response.data.jenis_kendaraan?.jumlah_seat} seat)"
                        binding.txtHargaProduk.text = Helper.formatRupiah(response.data.produk.harga)
                        binding.txtTglKeberangkatan.text = Helper.formatTanggalLengkapFromDate(response.data.keberangkatan?.tanggal_perjalanan)
                        binding.txtNotelpPemesan.text = response.data.user?.no_telp
                        var lokPenjemputanLat = 0.0
                        response.data.keberangkatan?.lokasi_jemput_lat?.let {
                            lokPenjemputanLat = it.toDouble()
                        }
                        var lokPenjemputanLong = 0.0
                        response.data.keberangkatan?.lokasi_jemput_lng?.let {
                            lokPenjemputanLong = it.toDouble()
                        }
                        val firstLatLng = LatLng(lokPenjemputanLat, lokPenjemputanLong)
                        lokasiJemput = DaftarLokasi("Lokasi Penjemputan", firstLatLng)
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
                                        lokasiList.clear()
                                        llTujuanWisata.removeAllViews()
                                        var sortedList = li.sortedWith(compareBy { it.order })
                                        sortedList.forEachIndexed { index, destinasi ->
                                            var lat = 0.0
                                            destinasi.tempat_wisata_data?.latitude?.let {
                                                lat = it.toDouble()
                                            }
                                            var lng = 0.0
                                            destinasi.tempat_wisata_data?.longitude?.let {
                                                lng = it.toDouble()
                                            }
                                            val newLatLng = LatLng(lat, lng)
                                            lokasiList.add(DaftarLokasi(destinasi.tempat_wisata_data?.nama ?: "", newLatLng))
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

        binding.lihatPeta.setOnClickListener {
            var tempList = arrayListOf<DaftarLokasi>()
            tempList.add(lokasiJemput)
            tempList.addAll(lokasiList)
            Log.d("kencana", "lok sbelum: " + tempList.toString())
            alurPemesananViewModel.setLokasiList(tempList)
            findNavController().navigate(R.id.action_transaksiFragment_to_lihatLokasiFragment)
        }

        val layoutDetail = binding.layoutDetailPemesanan
        layoutDetail.isDrawingCacheEnabled = true
        val bitmapp = layoutDetail.drawingCache
        binding.btnUnduhBukti.setOnClickListener {
            binding.lihatPeta.visibility = View.GONE
            val viewBitmap = getBitmapFromView(binding.layoutDetailPemesanan)
            saveBitmapToPicturesDirectory(requireContext(), viewBitmap, fileName)
            binding.lihatPeta.visibility = View.VISIBLE
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

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun saveBitmapToPicturesDirectory(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val contentResolver = context.contentResolver
        var imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri == null) {
            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(picturesDirectory, "$fileName.jpg")
            imageUri = Uri.fromFile(image)
        }

        try {
            val outputStream: OutputStream? = imageUri?.let { contentResolver.openOutputStream(it) }
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            }

            // Notify the MediaScanner to scan for the saved image
            if (imageUri != null) {
                context.makeToast("Bukti Pemesanan Berhasil Disimpan ke Galeri")
                MediaScannerConnection.scanFile(context, arrayOf(imageUri.path), arrayOf("image/jpeg"), null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}