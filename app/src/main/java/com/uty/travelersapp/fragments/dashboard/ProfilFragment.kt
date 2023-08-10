package com.uty.travelersapp.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.uty.travelersapp.MainActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.customs.MyAlertDialogBuilder
import com.uty.travelersapp.customs.MyAlertDialogType
import com.uty.travelersapp.databinding.FragmentProfilBinding
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

        binding.toolbarProfil.inflateMenu(R.menu.menu_profil)
        binding.toolbarProfil.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.menuitem_lihat_promo -> {
                    findNavController().navigate(R.id.action_navitem_profil_to_infoPromoFragment)
                }

                else -> {}
            }
            return@setOnMenuItemClickListener true
        }

        binding.btnUbahPassword.setOnClickListener {
            findNavController().navigate(R.id.action_navitem_profil_to_ubahPasswordFragment)
        }
        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_navitem_profil_to_aboutFragment)
        }

        binding.btnUbahProfil.setOnClickListener {
            findNavController().navigate(R.id.action_navitem_profil_to_editProfileFragment)
        }
        val user = firebaseAuth.currentUser
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
                            binding.imgProfil.load(pp) {
                                placeholder(R.drawable.ic_profile_pic_placeholder)
                                crossfade(true)
                            }

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