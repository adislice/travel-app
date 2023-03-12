package com.uty.travelersapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uty.travelersapp.MainActivity
import com.uty.travelersapp.databinding.FragmentProfilBinding
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth

class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogoutUser.setOnClickListener {

            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

}