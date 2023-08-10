package com.uty.travelersapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.PrakiraanCuacaAdapter
import com.uty.travelersapp.databinding.FragmentPrakiraanCuacaBinding
import com.uty.travelersapp.extensions.Helpers.Companion.fixBottomInsets
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.ForecastData
import com.uty.travelersapp.models.WeatherResponse
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.ZoneId
import java.util.Date

class PrakiraanCuacaFragment : Fragment() {
    private var _binding: FragmentPrakiraanCuacaBinding? = null
    private val binding get() = _binding!!
    private lateinit var prakiraanAdapter: PrakiraanCuacaAdapter
    private lateinit var rvPrakiraan: RecyclerView
    private var lat: String? = ""
    private var lng: String? = ""
    private var nama: String? = ""
    private var tgl: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrakiraanCuacaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiKey = "1d9056f2a7b12dd3df82902238c412d1"

        lat = arguments?.getString("LAT", "")
        lng = arguments?.getString("LNG", "")
        nama = arguments?.getString("NAMA", "")
        tgl = arguments?.getSerializable("TANGGAL_PERJALANAN") as? Date

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutPrakiraan.fixBottomInsets()

        if (!lat.isNullOrEmpty() && !lng.isNullOrEmpty() && tgl != null) {
            binding.txtPrakiraanJudul.text = "Prakiraan Cuaca di ${nama}"
            rvPrakiraan = binding.rvPrakiraan
            prakiraanAdapter = PrakiraanCuacaAdapter()
            rvPrakiraan.layoutManager = LinearLayoutManager(requireContext())
            rvPrakiraan.setHasFixedSize(true)
            rvPrakiraan.adapter = prakiraanAdapter

            val call = RetrofitClient.weatherApi.getForecastData(lat!!, lng!!, apiKey)
            call.enqueue(object: Callback<WeatherResponse>{
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        weatherResponse.let {
                            Log.d("kencana", "weather: " + it?.list?.first()?.weather?.first())
                            val arrayTmp = mutableListOf<ForecastData>()
                            it?.list?.forEach { forecast ->
                                val date = Date(forecast.dt!! * 1000L)
                                val cekTgl = areDatesSame(tgl!!, date)
                                if (cekTgl) {
                                    arrayTmp.add(forecast)
                                }
                            }

                            if (arrayTmp.size < 8) {
                                binding.txtPrakiraanJudul.text = "Prakiraan Cuaca di ${nama} belum tersedia. Prakiraan cuaca tersedia pada 5 hari sebelum tanggal keberangkatan"
                            } else {
                                prakiraanAdapter.updateList(arrayTmp)
                            }
//                            it?.list?.let { it1 -> prakiraanAdapter.updateList(it1) }
                        }
                    } else {
                        requireActivity().makeToast("Kesalahan saat mengambil data cuaca")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    requireActivity().makeToast("Network error")
                }

            })
        }






    }

    fun areDatesSame(date1: Date, date2: Date): Boolean {
        val localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        return localDate1 == localDate2
    }

}