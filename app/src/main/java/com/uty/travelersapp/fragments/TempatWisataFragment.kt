package com.uty.travelersapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
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
    private lateinit var tempatWisataViewModel: TempatWisataViewModel
    private lateinit var tempatWisataAdapter: ListTempatWisataAdapter

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
        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.isAppearanceLightStatusBars = true

        val toolbar = binding.toolbarTempatWisata
        toolbar.inflateMenu(R.menu.menu_searchview)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        var searchMenu = toolbar.menu.findItem(R.id.menuitem_searchview)
        val searchView = binding.searchviewTempatwisata
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE)


        searchView.addTransitionListener { searchView, previousState, newState ->
            if (newState == com.google.android.material.search.SearchView.TransitionState.HIDING) {
                bottomNav.visibility = View.VISIBLE
            } else if (newState == com.google.android.material.search.SearchView.TransitionState.SHOWING) {
                bottomNav.visibility = View.GONE
            }
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.menuitem_searchview) {
                searchView.show()
            }
            return@setOnMenuItemClickListener true
        }
        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requireActivity().makeToast("search " + searchView.text)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchView.isShowing) {
                searchView.hide()
            } else {
//                requireActivity().makeToast("back")
                findNavController().popBackStack()
            }
        }

        val swipeView: SwipeRefreshLayout = binding.swipeRefreshTempatwisata
        rvTempatWisata = binding.rvTempatWisata
        rvTempatWisata.layoutManager = LinearLayoutManager(requireContext())

        rvTempatWisata.setHasFixedSize(true)
        tempatWisataAdapter = ListTempatWisataAdapter()
        rvTempatWisata.adapter = tempatWisataAdapter

        tempatWisataViewModel = ViewModelProvider(this).get(TempatWisataViewModel::class.java)

        tempatWisataViewModel.allTempatWisata.observe(viewLifecycleOwner, Observer {
            tempatWisataAdapter.updateList(it)
            binding.loadingTempatwisata.visibility = View.GONE
            swipeView.isRefreshing = false
        })


        swipeView.setOnRefreshListener {
            tempatWisataViewModel.getAllTempatWisata()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}