package com.uty.travelersapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.ListPaketWisataAdapter
import com.uty.travelersapp.databinding.FragmentPaketWisataBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import com.uty.travelersapp.viewmodel.PaketWisataViewModel

class PaketWisataFragment : Fragment() {
    private lateinit var binding: FragmentPaketWisataBinding
    private lateinit var rvPaketWisata: RecyclerView
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private lateinit var paketWisataAdapter: ListPaketWisataAdapter
    private var paketWisataList = arrayListOf<PaketWisataItem>()
    private var searchQuery = ""

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

        val toolbar = binding.toolbarPaketWisata
        toolbar.inflateMenu(R.menu.menu_searchview)

        val inputSearch = binding.searchPaketwisata
        inputSearch.translationY = 0.0f
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.menuitem_searchview) {
                if (inputSearch.visibility == View.GONE) {
                    inputSearch.visibility = View.VISIBLE
                } else {
                    inputSearch.visibility = View.GONE
                }

            }
            return@setOnMenuItemClickListener true
        }

        inputSearch.editText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery = v.text.toString()
                filterSearch(v.text.toString())
            }
            true
        }

        if (searchQuery.isNotEmpty()) {
            inputSearch.visibility = View.VISIBLE
        }
        inputSearch.editText?.setText(searchQuery)

        rvPaketWisata = binding.rvPaketWisata
        rvPaketWisata.layoutManager = LinearLayoutManager(requireContext())
        rvPaketWisata.setHasFixedSize(true)
        paketWisataAdapter = ListPaketWisataAdapter()
        rvPaketWisata.adapter = paketWisataAdapter

        paketWisataViewModel.initAllPaketWisata()

        paketWisataViewModel.allPaketWisata.observe(viewLifecycleOwner, Observer {
            paketWisataList = ArrayList(it)
            if (searchQuery.isNotEmpty()) {
                filterSearch(searchQuery)
            } else {
                paketWisataAdapter.updateList(ArrayList(it))
            }

            binding.loadingPaketwisata.visibility = View.GONE
        })

    }

    private fun filterSearch(query: String) {
        val filteredList = ArrayList<PaketWisataItem>()

        for (paket in paketWisataList) {
            val matchName = paket.nama!!.contains(query, ignoreCase = true)
            val twList = paket.tempat_wisata_data!!.filter { tw -> tw.kota!!.contains(query, ignoreCase = true) }
            if (twList.isNotEmpty() || matchName) {
                filteredList.add(paket)
            }
        }
        paketWisataAdapter.updateList(filteredList)
        if (filteredList.isEmpty()) {
            requireActivity().makeToast("Pencarian \'${query}\' tidak ditemukan")
        }

    }


}