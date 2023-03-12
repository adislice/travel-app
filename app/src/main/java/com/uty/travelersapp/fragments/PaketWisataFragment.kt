package com.uty.travelersapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentPaketWisataBinding

class PaketWisataFragment : Fragment() {
    private lateinit var binding: FragmentPaketWisataBinding

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
}