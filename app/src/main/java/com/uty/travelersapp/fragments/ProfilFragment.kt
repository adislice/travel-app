package com.uty.travelersapp.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.uty.travelersapp.MainActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.customs.MyAlertDialogBuilder
import com.uty.travelersapp.customs.MyAlertDialogType
import com.uty.travelersapp.databinding.FragmentProfilBinding
import com.uty.travelersapp.extensions.Helpers.Companion.getUserPref
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.viewmodel.UserViewModel

class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
//    private val userViewModel: UserViewModel by activityViewModels()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivPro = binding.imgProfil

        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.isAppearanceLightStatusBars = true

        binding.btnLogoutUser.setOnClickListener {

            MyAlertDialogBuilder(requireContext(), MyAlertDialogType.WARNING).apply {
                setTitle("Keluar?")
                setBody("Anda yakin ingin keluar?")
                setConfirmButton("Keluar") {
                    firebaseAuth.signOut()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
                setCancelButton("Batal") {
                    close()
                }.show()
            }
        }

//        requireContext().getUserPref()?.let {
//            binding.txtProfilNama.text = it.nama
//            binding.txtProfilEmail.text = it.email
//
//        }

//        val toolbar = binding.toolbarProfil
//        toolbar.inflateMenu(R.menu.menu_fragment_profile)
//        toolbar.setOnMenuItemClickListener { menu ->
//            if (menu.itemId == R.id.menuitem_edit_profile) {
//                findNavController().navigate(R.id.action_navitem_profil_to_editProfileFragment)
//            }
//            return@setOnMenuItemClickListener true
//        }
        binding.btnUbahProfil.setOnClickListener {
            findNavController().navigate(R.id.action_navitem_profil_to_editProfileFragment)
        }
        val user = firebaseAuth.currentUser
//        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        Log.d("kencana", "awal: " + userViewModel.userId.value)
        user?.let { currUser ->
            userViewModel.setUser(user.uid)
            userViewModel.userDetailData.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                        binding.txtProfilNama.text = response.data.nama
                        binding.txtProfilEmail.text = response.data.email
                        response.data.profile_picture?.let { pp ->
                            Glide.with(requireActivity())
                                .load(pp)
                                .centerCrop()
                                .placeholder(R.drawable.image_placeholder)
                                .error(R.drawable.image_placeholder)
                                .into(binding.imgProfil)
                            binding.imgProfil.setColorFilter(null)
                            binding.imgProfil.imageTintList = null

                        }
                    }
                    is Response.Failure -> {
                        Log.d("kencana", response.errorMessage)
                    }

                    else -> {}
                }
            }

        }


    }

}