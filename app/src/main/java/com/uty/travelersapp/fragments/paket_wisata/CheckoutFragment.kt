package com.uty.travelersapp.fragments.paket_wisata

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.textfield.TextInputLayout
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentCheckoutBinding
import com.uty.travelersapp.extensions.Helpers.Companion.fixBottomInsets
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.Pemesanan
import com.uty.travelersapp.models.PemesananInsert
import com.uty.travelersapp.models.PemesananJenisKendaraan
import com.uty.travelersapp.models.PemesananKeberangkatan
import com.uty.travelersapp.models.PemesananPaketWisata
import com.uty.travelersapp.models.PemesananProduk
import com.uty.travelersapp.models.PemesananPromo
import com.uty.travelersapp.models.PemesananUser
import com.uty.travelersapp.models.Promo
import com.uty.travelersapp.utils.FirebaseUtils
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.AlurPemesananViewModel
import com.uty.travelersapp.viewmodel.PemesananViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val tujuanWisata = arrayListOf<String>()
    private var isTujuanWisataExpanded = true
    private val alurPemesananViewModel: AlurPemesananViewModel by activityViewModels()
    private val tujuanWisataList = arrayListOf<TempatWisataArrayItem>()
    private var hargaProduk = MutableLiveData<Double>(0.0)
    private var diskon = MutableLiveData<Double>(0.0)
    private var totalBayar = MutableLiveData<Double>(0.0)
    private var daftarDiskon = arrayListOf<PromoTemp>()
    private var promoTerpasang = PromoTemp("", 0)
    private var promoAktif=  MutableLiveData<Promo?>(null)
    private var potongan = 0.0
    private var idPromo = ""
    private val pemesananViewModel by activityViewModels<PemesananViewModel>()

    data class PromoTemp(val kode: String, val persen: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.kontenPesan.fixBottomInsets()

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        daftarDiskon.add(PromoTemp("NEWYEAR", 20))
        daftarDiskon.add(PromoTemp("JOGJASKUY", 30))

        val llTujuanWisata = binding.llTlTujuanWisata
        diskon.value = 0.0

        // get data paket wisata dari viewmodel
        alurPemesananViewModel.paketWisataTerpilih.observe(viewLifecycleOwner) { pw ->
            binding.txtNamaPaket.text = pw.nama
            binding.txtJamKeberangkatan.text = pw.jam_keberangkatan
            binding.imgThumbPaket.load(pw.foto?.firstOrNull()) {
                crossfade(true)
                placeholder(R.drawable.image_placeholder)
            }
        }
        binding.txtNamaProduk.visibility = View.GONE
        // get data produk paket wisata dari viewmodel
        alurPemesananViewModel.produkTerpilih.observe(viewLifecycleOwner) { produk ->
            val namaPaket = alurPemesananViewModel.paketWisataTerpilih.value?.nama
            binding.txtHargaProduk.text = Helper.formatRupiah(produk.harga!!)
            binding.txtTransaksiNamaPaket.text = "${namaPaket} (${produk?.jenis_kendaraan?.nama})"
            binding.txtTransaksiHargaPaket.text = Helper.formatRupiah(produk.harga)
            hargaProduk.value = produk.harga!!
            binding.txtNamaArmada.text = produk.jenis_kendaraan?.nama
            binding.txtJumlahPenumpang.text = "${produk?.jenis_kendaraan?.jumlah_seat} seat"
            hitungTotalBayar()
        }

        alurPemesananViewModel.tujuanWisata.observe(viewLifecycleOwner) { tujuan ->
            tujuanWisataList.clear()
            tujuan.forEachIndexed { index, destinasi ->
                val infl = layoutInflater.inflate(R.layout.item_timeline_tempat_wisata, null, false)
                infl.findViewById<TextView>(R.id.tl_text).text = destinasi.tempat_wisata_data?.nama

                if (index == 0) {
                    infl.findViewById<View>(R.id.tl_garis_atas).visibility = View.INVISIBLE
                }
                if (index == tujuan.size - 1) {
                    infl.findViewById<View>(R.id.tl_garis_bawah).visibility = View.INVISIBLE
                }
                llTujuanWisata.addView(infl)
            }

        }

        alurPemesananViewModel.namaPemesan.observe(viewLifecycleOwner) {
            binding.txtNamaPemesan.text = it
        }

        alurPemesananViewModel.noTelpPemesan.observe(viewLifecycleOwner) {
            binding.txtNotelpPemesan.text = it
        }

        alurPemesananViewModel.tanggalPerjalanan.observe(viewLifecycleOwner) {
            val formatted = Helper.formatTanggalLengkap(it)
            binding.txtTglKeberangkatan.text = formatted
        }

        alurPemesananViewModel.lokasiPenjemputan.observe(viewLifecycleOwner) {
            val address = Helper.getAddress(requireActivity(), it.latitude, it.longitude)
            binding.txtAlamatJemput.text = address
        }

        totalBayar.observe(viewLifecycleOwner) {
            val totalFormatted = Helper.formatRupiah(it)
            binding.txtTotalBayar.text = totalFormatted
        }

        binding.btnInputPromo.setOnClickListener {
            showPromoCodeDialog()
        }

        promoAktif.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.txtTransaksiDiskonNama.text = "Potongan ${it.nama}"
                binding.txtTransaksiDiskonPotongan.text = "- " + Helper.formatRupiah(potongan)
            }
            else {
                binding.txtTransaksiDiskonNama.text = " "
                binding.txtTransaksiDiskonPotongan.text = " "
            }

        }

        binding.btnProsesTransaksi.setOnClickListener {
            tambahTransaksi()
        }

    }

    private fun showPromoCodeDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Masukkan Kode Promo")

        val alertLayout = layoutInflater.inflate(R.layout.dialog_input_promo, null)
        builder.setView(alertLayout)
        alertLayout.findViewById<TextInputLayout>(R.id.input_promo).editText?.setText(promoTerpasang.kode)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            val promoCode = alertLayout.findViewById<TextInputLayout>(R.id.input_promo).editText?.text.toString().uppercase(
                Locale.getDefault())
            val matchedPromo = daftarDiskon.find { it.kode.uppercase(Locale.getDefault()) == promoCode }


            lifecycleScope.launch(Dispatchers.Main) {
                val cek = cekPromo(promoCode)
                if (cek != null) {
                    var msg = "Selamat! Anda mendapatkan diskon ${cek.persen}% "
                    if (cek.max_potongan!! > 0.0) {
                        msg += "(max: ${Helper.formatRupiah(cek.max_potongan)})"

                    }
                    val pot = hargaProduk.value!! * cek.persen!! / 100.0
                    if (pot > cek.max_potongan) {
                        potongan = cek.max_potongan
                    } else {
                        potongan = pot
                    }
                    cek.id?.let {idP -> idPromo = idP}
                    promoAktif.value = cek
                    showDialog("Kode Promo Valid", msg)
                    hitungTotalBayar()
                    binding.txtKodePromo.text = "Promo digunakan \'${promoAktif.value?.kode}\'"
                }
                dialog.dismiss()
            }

        }

        builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.setNeutralButton("Hapus Promo") { dialog, _ ->
            diskon.value = 0.0
            potongan = 0.0
            hitungTotalBayar()
            promoAktif.value = null
            promoTerpasang = PromoTemp("", 0)
            binding.txtKodePromo.text = "Masukkan kode promo"
        }

        builder.create().show()


    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.create().show()
    }

    fun hitungTotalBayar() {
        val total = hargaProduk.value!! - potongan
        totalBayar.value = total
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun tambahTransaksi() {
        val spinner = Helper.buildLoadingSpinner(requireActivity(), "Memproses Transaksi", "Silahkan tunggu...")
        lifecycleScope.launch {
            spinner.show()
            delay(5000)
        }
        val sekarang = LocalDateTime.now()
        val waktu24Jam = sekarang.plusHours(24)
        val batasBayar = Date.from(waktu24Jam.atZone(ZoneId.systemDefault()).toInstant())
        val pw = PemesananPaketWisata(
            id = alurPemesananViewModel.paketWisataTerpilih.value?.id,
            nama = alurPemesananViewModel.paketWisataTerpilih.value?.nama,
            foto = alurPemesananViewModel.paketWisataTerpilih.value?.foto?.get(0)
        )
        val produk = PemesananProduk(
            harga = alurPemesananViewModel.produkTerpilih.value?.harga,
            id = alurPemesananViewModel.produkTerpilih.value?.id
        )
        val promo = PemesananPromo(
            id = "",
            nama = promoTerpasang.kode,
            kode = promoTerpasang.kode,
            persen = promoTerpasang.persen.toDouble(),
            potongan = diskon.value
        )

        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateKeberangkatan = inputFormat.parse(alurPemesananViewModel.tanggalPerjalanan.value!!)
        val lokLat = if(alurPemesananViewModel.lokasiPenjemputan.value?.latitude != null) alurPemesananViewModel.lokasiPenjemputan.value?.latitude.toString() else "0"
        val lokLong = if(alurPemesananViewModel.lokasiPenjemputan.value?.longitude != null) alurPemesananViewModel.lokasiPenjemputan.value?.longitude.toString() else "0"
        val keberangkatan = PemesananKeberangkatan(
            dateKeberangkatan,
            lokLat,
            lokLong
        )

        val dataTransaksi = PemesananInsert(
            kode_pemesanan = Helper.generateTransactionCode(),
            total_bayar = totalBayar.value,
            status = Status.DIPROSES,
            paket_wisata_id = alurPemesananViewModel.paketWisataTerpilih.value?.id,
            produk_harga = alurPemesananViewModel.produkTerpilih.value?.harga,
            produk_id = alurPemesananViewModel.produkTerpilih.value?.id,
            promo_id = promoAktif.value?.id,
            promo_potongan = potongan,
            user_id = MyUser.user?.id,
            user_nama = alurPemesananViewModel.namaPemesan.value,
            user_no_telp = alurPemesananViewModel.noTelpPemesan.value,
            tanggal_keberangkatan = dateKeberangkatan,
            jam_keberangkatan = alurPemesananViewModel.paketWisataTerpilih.value?.jam_keberangkatan,
            lokasi_jemput_lat = lokLat,
            lokasi_jemput_lng = lokLong,
        )
        pemesananViewModel.insertPemesanan(MyUser.user?.nama!!, dataTransaksi).observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    spinner.show()
                }
                is Response.Success -> {
                    Log.d("kencana", "Membuka fragment transaksi")
                    val bundle = Bundle()
                    bundle.putString(IntentKey.TRANSAKSI_ID, response.data)
                    findNavController().navigate(R.id.action_checkoutFragment_to_transaksiFragment, bundle)
                    spinner.dismiss()
                }
                is Response.Failure -> {
                    spinner.dismiss()
                }

                else -> {
                    spinner.dismiss()
                }
            }
        }
    }


    suspend fun cekPromo(kodePromo: String): Promo? {
        val result = FirebaseUtils.firebaseDatabase.collection("promo").whereEqualTo("kode", kodePromo.uppercase()).get().await()
        if (result.isEmpty) {
            showDialog("Kode Promo Tidak Valid", "Kode promo yang dimasukkan tidak valid.")
        }

        for (promoSnap in result.documents) {
            var promo = promoSnap.toObject(Promo::class.java)
            promo?.id = promoSnap.id
            val isValid = isTodayInRange(promo?.tanggal_mulai!!, promo.tanggal_akhir!!)
            if (isValid) {
//                var msg = "Selamat! Anda mendapatkan diskon ${promo.persen}% "
//                if (promo.max_potongan!! > 0.0) {
//                    msg += "(max: ${Helper.formatRupiah(promo.max_potongan)})"
//                }
//                showDialog("Kode Promo Valid", msg)
                return promo
            } else {
                showDialog("Kode Promo Tidak Valid", "Masa berlaku promo sudah habis")
            }
        }

        return null

    }

    fun isTodayInRange(minDate: Date, maxDate: Date): Boolean {
        try {
            val currentDate = Date()

            return currentDate.after(minDate) && currentDate.before(maxDate)
        } catch (e: Exception) {
            Log.d("kencana", "kesalahan promo: " + e.message)
            return false
        }

    }

}