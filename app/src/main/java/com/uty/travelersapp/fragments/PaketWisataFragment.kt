package com.uty.travelersapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.adapters.ListPaketWisataAdapter
import com.uty.travelersapp.databinding.FragmentPaketWisataBinding
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import com.uty.travelersapp.viewmodel.PaketWisataViewModel

class PaketWisataFragment : Fragment() {
    private lateinit var binding: FragmentPaketWisataBinding
    private lateinit var rvPaketWisata: RecyclerView
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private lateinit var paketWisataAdapter: ListPaketWisataAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaketWisataBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPaketWisata = binding.rvPaketWisata
        rvPaketWisata.layoutManager = LinearLayoutManager(requireContext())
        rvPaketWisata.setHasFixedSize(true)
        paketWisataAdapter = ListPaketWisataAdapter()
        rvPaketWisata.adapter = paketWisataAdapter

        paketWisataViewModel.initAllPaketWisata()

        paketWisataViewModel.allPaketWisata.observe(viewLifecycleOwner, Observer {
            paketWisataAdapter.updateList(it)
            binding.loadingPaketwisata.visibility = View.GONE
        })

    }

}