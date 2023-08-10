package com.uty.travelersapp.fragments.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uty.travelersapp.adapters.ListRiwayatPemesananAdapter
import com.uty.travelersapp.databinding.FragmentRiwayatPemesananBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.PemesananViewModel

class RiwayatPemesananFragment : Fragment() {
    private var _binding: FragmentRiwayatPemesananBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvRiwayat: RecyclerView
    private lateinit var riwayatAdapter: ListRiwayatPemesananAdapter
    private val pemesananViewModel by activityViewModels<PemesananViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatPemesananBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        riwayatAdapter = ListRiwayatPemesananAdapter()
        rvRiwayat = binding.rvPesanan
        rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        rvRiwayat.setHasFixedSize(true)
        rvRiwayat.adapter = riwayatAdapter
        val userId = MyUser.user?.id
        if (userId == null) {
            requireContext().makeToast("User id tidak ada")
            return
        }
        pemesananViewModel.getUserPemesanan(userId).observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Success -> {
                    riwayatAdapter.updateList(response.data)
                    binding.loadingPesanna.visibility = View.GONE
                }
                is Response.Failure -> {
                    requireContext().makeToast("Error: ${response.errorMessage}")
                }

                else -> {}
            }
        }


    }

}