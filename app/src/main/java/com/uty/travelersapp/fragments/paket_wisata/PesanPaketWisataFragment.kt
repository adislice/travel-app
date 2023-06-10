package com.uty.travelersapp.fragments.paket_wisata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        produkId = arguments?.getString(IntentKey.PRODUK_PAKET_WISATA_ID, "")!!

        paketWisataViewModel.getProdukPaketWisata.observe(viewLifecycleOwner) { response ->
            when(response){
                is Response.Success -> {
                    val selected = response.data.find { item -> item.id == produkId }
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
                    Glide.with(requireActivity())
                        .load(response.data.thumbnail_foto)
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
            }
        }

        binding.inputNama.editText?.setText(MyUser.user?.nama)
        binding.inputNoTelp.editText?.setText(MyUser.user?.no_telp)
        binding.btnPickFromMap.setOnClickListener {

            findNavController().navigate(R.id.action_pesanPaketWisataFragment_to_locationPickerFragment)
//            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("MAP_PICKER_LAT_LNG")?.observe(viewLifecycleOwner) { result ->
//                 Log.d("kencana", "result getted: " + result.toString())
//            }
        }

        // get data from map picker
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("MAP_PICKER_LAT_LNG")?.observe(viewLifecycleOwner) { result ->
            Log.d("kencana", "result getted: " + result.toString())
            if (result != null) {
                val alamat = Helper.getAddress(requireActivity(), result.latitude, result.longitude)
                binding.inputLokasiJemput.editText?.setText(alamat)
            }
        }


    }

}