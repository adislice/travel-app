package com.uty.travelersapp.fragments

import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.uty.travelersapp.R


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_login_fragment)
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        val  upArrow = ContextCompat.getDrawable(requireActivity(), com.google.android.material.R.drawable.abc_ic_ab_back_material)
        upArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_main), PorterDuff.Mode.SRC_ATOP);

        toolbar.navigationIcon = upArrow
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }





}