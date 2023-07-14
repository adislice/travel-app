package com.uty.travelersapp.fragments.paket_wisata

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentCheckoutBinding
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.Transaksi
import com.uty.travelersapp.models.TransaksiKeberangkatan
import com.uty.travelersapp.models.TransaksiPaketWisata
import com.uty.travelersapp.models.TransaksiProduk
import com.uty.travelersapp.models.TransaksiPromo
import com.uty.travelersapp.models.TransaksiUser
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.PemesananViewModel
import com.uty.travelersapp.viewmodel.TransaksiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val pemesananViewModel: PemesananViewModel by activityViewModels()
    private val tujuanWisataList = arrayListOf<TempatWisataArrayItem>()
    private var hargaProduk = MutableLiveData<Double>(0.0)
    private var diskon = MutableLiveData<Double>(0.0)
    private var totalBayar = MutableLiveData<Double>(0.0)
    private var daftarDiskon = arrayListOf<Promo>()
    private var promoTerpasang = Promo("", 0)
    private val transaksiViewModel by activityViewModels<TransaksiViewModel>()

    data class Promo(val kode: String, val persen: Int)

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.kontenPesan) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val pb = insets.bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                pb
            )

            WindowInsetsCompat.CONSUMED
        }

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        daftarDiskon.add(Promo("NEWYEAR", 20))
        daftarDiskon.add(Promo("JOGJASKUY", 30))

        val llTujuanWisata = binding.llTlTujuanWisata
        diskon.value = 0.0

        // get data paket wisata dari viewmodel
        pemesananViewModel.paketWisataTerpilih.observe(viewLifecycleOwner) { pw ->
            binding.txtNamaPaket.text = pw.nama
        }
        binding.txtNamaProduk.visibility = View.GONE
        // get data produk paket wisata dari viewmodel
        pemesananViewModel.produkTerpilih.observe(viewLifecycleOwner) { produk ->
            val namaPaket = pemesananViewModel.paketWisataTerpilih.value?.nama
            binding.txtHargaProduk.text = Helper.formatRupiah(produk.harga!!)
            binding.txtTransaksiNamaPaket.text = "${namaPaket} (${produk?.jenis_armada?.kapasitas_min.toString()} - ${produk?.jenis_armada?.kapasitas_max.toString()} orang)"
            binding.txtTransaksiHargaPaket.text = Helper.formatRupiah(produk.harga)
            hargaProduk.value = produk.harga!!
            binding.txtNamaArmada.text = produk.jenis_armada?.nama
            binding.txtJumlahPenumpang.text = "${produk?.jenis_armada?.kapasitas_min.toString()} - ${produk?.jenis_armada?.kapasitas_max.toString()} penumpang"
            hitungTotalBayar()
        }

        pemesananViewModel.tujuanWisata.observe(viewLifecycleOwner) { tujuan ->
            tujuanWisataList.clear()
//            tujuan.forEach { item ->
//                tujuanWisataList.add(item)
//            }
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

        pemesananViewModel.namaPemesan.observe(viewLifecycleOwner) {
            binding.txtNamaPemesan.text = it
        }

        pemesananViewModel.noTelpPemesan.observe(viewLifecycleOwner) {
            binding.txtNotelpPemesan.text = it
        }

        pemesananViewModel.tanggalPerjalanan.observe(viewLifecycleOwner) {
            val formatted = Helper.formatTanggalLengkap(it)
            binding.txtTglKeberangkatan.text = formatted
        }

        pemesananViewModel.lokasiPenjemputan.observe(viewLifecycleOwner) {
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

        diskon.observe(viewLifecycleOwner) {
            binding.txtTransaksiDiskonNama.text = "Potongan Promo ${promoTerpasang.persen}%"
            binding.txtTransaksiDiskonPotongan.text = "- " + Helper.formatRupiah(it)
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

            if (matchedPromo != null) {
                // Kode promo valid
                promoTerpasang = matchedPromo
                showDialog(
                    "Kode Promo Valid",
                    "Kode promo berhasil digunakan. Anda mendapat diskon ${matchedPromo.persen}%"
                )
                val d = matchedPromo.persen / 100.0
                diskon.value = d * hargaProduk.value!!
                binding.txtKodePromo.text = "Kode promo digunakan: ${matchedPromo.kode}"
                hitungTotalBayar()
            } else {
                // Kode promo tidak valid
                showDialog("Kode Promo Tidak Valid", "Kode promo yang dimasukkan tidak valid.")
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.setNeutralButton("Hapus Promo") { dialog, _ ->
            diskon.value = 0.0
            hitungTotalBayar()
            promoTerpasang = Promo("", 0)
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
        val total = hargaProduk.value!! - diskon.value!!
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
        val pw = TransaksiPaketWisata(
            id = pemesananViewModel.paketWisataTerpilih.value?.id,
            nama = pemesananViewModel.paketWisataTerpilih.value?.nama,
            foto = pemesananViewModel.paketWisataTerpilih.value?.foto?.get(0)
        )
        val produk = TransaksiProduk(
            nama = pemesananViewModel.produkTerpilih.value?.jenis_armada?.kapasitas_min.toString() + " - " + pemesananViewModel.produkTerpilih.value?.jenis_armada?.kapasitas_max.toString() + " orang",
            harga = pemesananViewModel.produkTerpilih.value?.harga,
            id = pemesananViewModel.produkTerpilih.value?.id
        )
        val promo = TransaksiPromo(
            id = "",
            kode = promoTerpasang.kode,
            diskon_persen = promoTerpasang.persen.toDouble(),
            potongan = diskon.value
        )
        val user = TransaksiUser(
            id = MyUser.user?.id,
            nama = pemesananViewModel.namaPemesan.value,
            no_telp = pemesananViewModel.noTelpPemesan.value
        )
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateKeberangkatan = inputFormat.parse(pemesananViewModel.tanggalPerjalanan.value!!)
        val lokLat = if(pemesananViewModel.lokasiPenjemputan.value?.latitude != null) pemesananViewModel.lokasiPenjemputan.value?.latitude.toString() else "0"
        val lokLong = if(pemesananViewModel.lokasiPenjemputan.value?.longitude != null) pemesananViewModel.lokasiPenjemputan.value?.longitude.toString() else "0"
        val keberangkatan = TransaksiKeberangkatan(
            dateKeberangkatan,
            lokLat,
            lokLong
        )
        val dataTransaksi = Transaksi(
            kode_transaksi = Helper.generateTransactionCode(),
            tanggal = Date(),
            total_bayar = totalBayar.value,
            batas_pembayaran = batasBayar,
            status = Status.BELUM_BAYAR,
            paket_wisata = pw,
            produk = produk,
            promo = promo,
            user = user,
            keberangkatan = keberangkatan
        )
        transaksiViewModel.insertTransaksi(MyUser.user?.nama!!, dataTransaksi).observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    spinner.show()
                }
                is Response.Success -> {
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

}