package com.uty.travelersapp.fragments.profil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uty.travelersapp.R
import com.uty.travelersapp.adapters.ListPromoAdapter
import com.uty.travelersapp.databinding.FragmentInfoPromoBinding
import com.uty.travelersapp.models.Promo
import com.uty.travelersapp.utils.FirebaseUtils


class InfoPromoFragment : Fragment() {
    private var _binding: FragmentInfoPromoBinding? = null
    private val binding get() = _binding!!
    private lateinit var promoAdapter: ListPromoAdapter
    private lateinit var rvPromo: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


        promoAdapter = ListPromoAdapter()
        rvPromo = binding.rvPromo
        rvPromo.layoutManager = LinearLayoutManager(requireContext())
        rvPromo.setHasFixedSize(true)
        rvPromo.adapter = promoAdapter

        getAllPromo()

    }

    fun getAllPromo() {
        FirebaseUtils.firebaseDatabase.collection("promo").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val promo = task.result.toObjects(Promo::class.java)
                promoAdapter.updateList(promo)
            } else {
                Log.d("kencana", "promo: " + task.exception?.message)
            }
        }
    }



}