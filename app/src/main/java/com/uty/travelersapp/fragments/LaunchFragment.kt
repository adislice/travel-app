package com.uty.travelersapp.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.uty.travelersapp.R

/**
 * A simple [Fragment] subclass.
 * Use the [LaunchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LaunchFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogin = view.findViewById<Button>(R.id.btn_login_user)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_launch)


        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_launchFragment_to_loginFragment)
        }
        val btnRegister = view.findViewById<Button>(R.id.btn_register_user)
        btnRegister.setOnClickListener{
            findNavController().navigate(R.id.action_launchFragment_to_registerFragment)
        }


    }


}