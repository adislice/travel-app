package com.uty.travelersapp.fragments.paket_wisata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentPesanPaketWisataBinding
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.PaketWisataViewModel
import com.uty.travelersapp.viewmodel.PemesananViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale


class PesanPaketWisataFragment : Fragment() {
    private lateinit var binding: FragmentPesanPaketWisataBinding
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private var paketId: String = ""
    private var produkId: String = ""
    private var selectedProduk: ProdukPaketWisata? = null
    private var tanggalPerjalanan: Long = 0
    private val pemesananViewModel: PemesananViewModel by activityViewModels()

    private var nama = MutableStateFlow("")
    private var tanggal = MutableStateFlow("")
    private var noTelp = MutableStateFlow("")
    private var lokasiJemput = MutableStateFlow("")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPesanPaketWisataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayout()

        produkId = arguments?.getString(IntentKey.PRODUK_PAKET_WISATA_ID, "")!!

        // cek input
        with(binding) {
            inputNama.editText?.doOnTextChanged { text, start, before, count ->
                nama.value = text.toString()
            }
            inputTanggal.editText?.doOnTextChanged { text, start, before, count ->
                tanggal.value = text.toString()
            }
            inputNoTelp.editText?.doOnTextChanged { text, start, before, count ->
                noTelp.value = text.toString()
            }
            inputLokasiJemput.editText?.doOnTextChanged { text, start, before, count ->
                lokasiJemput.value = text.toString()
            }
        }

        lifecycleScope.launch {
            formIsValid.collect {
                binding.btnLanjutPesan.isEnabled = it
            }
        }

        paketWisataViewModel.getProdukPaketWisata.observe(viewLifecycleOwner) { response ->
            when(response){
                is Response.Success -> {
                    val selected = response.data.find { item -> item.id == produkId }
                    pemesananViewModel.setProdukTerpilih(selected!!)
                    this.selectedProduk = selected
                    binding.kirara.text = "Produk paket wisata dipilih: " + selected?.nama
//                    binding.txtNamaProduk.text = selected?.nama
                    binding.txtNamaProduk.visibility = View.GONE
                    binding.txtHargaProduk.text = "Rp. " + selected?.harga.toString()
                    binding.txtNamaArmada.text = selected?.jenis_armada?.nama
                    binding.txtJumlanPenumpang.text = "${selected?.jenis_armada?.kapasitas_min.toString()} - ${selected?.jenis_armada?.kapasitas_max.toString()} penumpang"
                }

                else -> {}
            }
        }

        paketId = arguments?.getString(IntentKey.PAKET_WISATA_ID, "")!!

        paketWisataViewModel.setPaketWisataId(paketId)
        paketWisataViewModel.getDetailPaketWisata.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Success -> {
                    pemesananViewModel.setPaketWisataTerpilih(response.data)
                    Glide.with(requireActivity())
                        .load(response.data.foto?.firstOrNull())
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(binding.imgThumbPaket)
                    binding.txtNamaPaket.text = response.data.nama
//                    binding.txtNamaPaket.visibility = View.GONE

                }

                else -> {}
            }
        }

        binding.inputTanggal.editText?.setOnClickListener {
            val today = LocalDate.now(ZoneId.systemDefault())
            val date1bulan = today.plusMonths(1)
            val p = System.currentTimeMillis()

//            val dateValidatorMin: DateValidator = DateValidatorPointForward.from(start)
//            val dateValidatorMax: DateValidator = DateValidatorPointBackward.before(max.getTimeInMillis())
            val dateValidatorMin: DateValidator = DateValidatorPointForward.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            val dateValidatorMax: DateValidator = DateValidatorPointBackward.before(date1bulan.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

            val listValidators = ArrayList<DateValidator>()
            listValidators.apply {
                add(dateValidatorMin)
                add(dateValidatorMax)
            }

            val validators = CompositeDateValidator.allOf(listValidators)
            val constraintBuilder = CalendarConstraints.Builder()
                .setValidator(validators)

            val constraintsBuilderRange = constraintBuilder.build()

            val datePicker = MaterialDatePicker.Builder.datePicker().apply {
                setTitleText("Pilih Tanggal")
                setCalendarConstraints(constraintsBuilderRange)
                if (tanggalPerjalanan != 0L) {
                    setSelection(tanggalPerjalanan)
                }
            }.build()

            datePicker.show(childFragmentManager, "PESAN_DATEPICKER")
            datePicker.addOnPositiveButtonClickListener {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.format(it)
                binding.inputTanggal.editText?.setText(date)
                tanggalPerjalanan = it
                pemesananViewModel.setTanggalPerjalanan(date)
            }
        }

        pemesananViewModel.tujuanWisata.observe(viewLifecycleOwner) {
            Log.d("kencana", "destinasi: " + it.toString())
        }

        binding.inputNama.editText?.setText(MyUser.user?.nama)
        binding.inputNoTelp.editText?.setText(MyUser.user?.no_telp)
        binding.btnPickFromMap.setOnClickListener {

            findNavController().navigate(R.id.action_pesanPaketWisataFragment_to_locationPickerFragment)
//            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("MAP_PICKER_LAT_LNG")?.observe(viewLifecycleOwner) { result ->
//                 Log.d("kencana", "result getted: " + result.toString())
//            }
        }

        pemesananViewModel.tanggalPerjalanan.observe(viewLifecycleOwner) {data->
            binding.inputTanggal.editText?.setText(data)
        }
        pemesananViewModel.lokasiPenjemputan.observe(viewLifecycleOwner) {data->
            val alamat = Helper.getAddress(requireActivity(), data.latitude, data.longitude)
            binding.inputLokasiJemput.editText?.setText(alamat)
        }

        // get data from map picker
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("MAP_PICKER_LAT_LNG")?.observe(viewLifecycleOwner) { result ->
            Log.d("kencana", "result getted: " + result.toString())
            if (result != null) {
                val alamat = Helper.getAddress(requireActivity(), result.latitude, result.longitude)
                binding.inputLokasiJemput.editText?.setText(alamat)
            }
        }

        binding.btnLanjutPesan.setOnClickListener {
            pemesananViewModel.setNamaPemesan(binding.inputNama.editText!!.text.toString())
            pemesananViewModel.setNoTelpPemesan(binding.inputNoTelp.editText!!.text.toString())
            Log.d("kencana", "vm pemesanan: lok: " + pemesananViewModel.lokasiPenjemputan.value + ", tgl: " + pemesananViewModel.tanggalPerjalanan.value)
            findNavController().navigate(R.id.action_pesanPaketWisataFragment_to_checkoutFragment)
        }

    }

    fun initializeLayout() {
        val konten = binding.kontenPesan
        ViewCompat.setOnApplyWindowInsetsListener(konten) { view, windowInsets ->
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
    }

    private var formIsValid = combine(nama, noTelp, lokasiJemput, tanggal) {nama, noTelp, lokasiJemput, tanggal ->
        val isNamaValid = nama.isNotEmpty()
        val isNoTelpValid = noTelp.isNotEmpty()
        val isLokasiJemputValid = lokasiJemput.isNotEmpty()
        val isTanggalValid = tanggal.isNotEmpty()
        val msg = "tidak boleh kosong"

        binding.inputNama.apply {
            error = if (isNamaValid) null else "Nama $msg"
            isErrorEnabled = !isNamaValid
        }
        binding.inputNoTelp.apply {
            error = if (isNoTelpValid) null else "No Telepon $msg"
            isErrorEnabled = !isNoTelpValid
        }
        binding.inputTanggal.apply {
            error = if (isTanggalValid) null else "Tanggal $msg"
            isErrorEnabled = !isTanggalValid
        }
        binding.inputLokasiJemput.apply {
            error = if (isLokasiJemputValid) null else "Lokasi penjemputan $msg"
            isErrorEnabled = !isLokasiJemputValid
        }

        isNamaValid and isNoTelpValid and isTanggalValid and isLokasiJemputValid
    }

}