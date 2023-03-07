package com.uty.travelersapp.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val upArrow = ContextCompat.getDrawable(requireActivity(), com.google.android.material.R.drawable.abc_ic_ab_back_material)
        upArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_main), PorterDuff.Mode.SRC_ATOP);

        binding.toolbarRegisterFragment.navigationIcon = upArrow
        binding.toolbarRegisterFragment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }


}