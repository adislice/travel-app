package com.uty.travelersapp.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.ListTempatWisataAdapter
import com.uty.travelersapp.databinding.FragmentTempatWisataBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.viewmodel.TempatWisataViewModel


class TempatWisataFragment : Fragment() {
    private lateinit var binding: FragmentTempatWisataBinding
    private lateinit var rvTempatWisata: RecyclerView
    private val tempatWisataViewModel by activityViewModels<TempatWisataViewModel>()
    private lateinit var tempatWisataAdapter: ListTempatWisataAdapter
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            searchQuery = it.getString("SEARCH_QUERY")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTempatWisataBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarTempatWisata
        toolbar.inflateMenu(R.menu.menu_searchview)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)


        val inputSearch = binding.searchTempatwisata
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


        rvTempatWisata = binding.rvTempatWisata
        rvTempatWisata.layoutManager = LinearLayoutManager(requireContext())

        rvTempatWisata.setHasFixedSize(true)
        tempatWisataAdapter = ListTempatWisataAdapter()
        rvTempatWisata.adapter = tempatWisataAdapter


        tempatWisataViewModel.allTempatWisata.observe(viewLifecycleOwner) { response ->
            tempatWisataAdapter.updateList(response)
            binding.loadingTempatwisata.visibility = View.GONE
        }

//        tempatWisataViewModel.cobaAllTempatWisata.observe(viewLifecycleOwner) { response ->
//            tempatWisataAdapter.updateList(response)
//            binding.loadingTempatwisata.visibility = View.GONE
//        }

        binding.searchTempatwisata.editText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                requireContext().makeToast("search: " + v.text.toString())
                tempatWisataViewModel.setSearchQuery(v.text.toString())
                tempatWisataViewModel.performSearch(v.text.toString())
                searchQuery = v.text.toString()
            }
            true
        }

        Log.d("kencana", searchQuery)
        if (searchQuery.isNotEmpty()) {
            inputSearch.visibility = View.VISIBLE
        }
        binding.searchTempatwisata.editText?.setText(searchQuery)


    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", searchQuery)
    }


}