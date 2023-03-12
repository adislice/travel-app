package com.uty.travelersapp.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.uty.travelersapp.DashboardActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentLoginBinding
import com.uty.travelersapp.extensions.Helpers.Companion.snackbarDismiss
import com.uty.travelersapp.models.UserModel
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginUsername: String
    private lateinit var loginPassword: String
    private lateinit var loginInputsArray: Array<TextInputLayout>
    private lateinit var originalText: CharSequence

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar =
            view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_login_fragment)
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(
            requireActivity(), com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        upArrow?.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.blue_main), PorterDuff.Mode.SRC_ATOP
        )

        toolbar.navigationIcon = upArrow
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        loginInputsArray = arrayOf(binding.inputLoginUsername, binding.inputLoginPassword)

        binding.btnLoginGaskan.setOnClickListener {
            login()
        }
    }

    fun login() {
        loginUsername = binding.inputLoginUsername.editText?.text.toString()
        loginPassword = binding.inputLoginPassword.editText?.text.toString()

        val spec = CircularProgressIndicatorSpec(
            requireActivity(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        val progressIndicatorDrawable =
            IndeterminateDrawable.createCircularDrawable(requireActivity(), spec)
        originalText = binding.btnLoginGaskan.text
        if (isInputFieldsLengkap()) {
//            Toast.makeText(requireContext(), "oke", Toast.LENGTH_LONG).show()
            binding.btnLoginGaskan.apply {
                icon = progressIndicatorDrawable
                isEnabled = false
                text = "Sedang login..."
            }
            getEmailFromDB { userDb ->
                if (userDb != null) {
                    userDb.email?.let { email ->
                        firebaseAuth.signInWithEmailAndPassword(email, loginPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    if (!task.result.user!!.isEmailVerified) {
                                        snackbarDismiss(
                                            "Akun belum terverifikasi. Silahkan cek email Anda dan klik tautan verifikasi.",
                                            binding.btnLoginGaskan
                                        )
                                        binding.btnLoginGaskan.apply {
                                            icon = null
                                            isEnabled = true
                                            text = originalText
                                        }
                                    } else {
                                        Snackbar.make(
                                            binding.btnLoginGaskan,
                                            "Login berhasil " + task.result.user?.email,
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                        val profileUpdate = userProfileChangeRequest {
                                            displayName = userDb.nama?.trim()
                                        }
                                        task.result.user!!.updateProfile(profileUpdate)
                                            .addOnCompleteListener { updateProfileTask ->
                                                if (updateProfileTask.isSuccessful) {
                                                    Log.d("firebase", "User profile updated")
                                                }
                                                startActivity(
                                                    Intent(
                                                        requireActivity(),
                                                        DashboardActivity::class.java
                                                    )
                                                )
                                                binding.btnLoginGaskan.apply {
                                                    icon = null
                                                    isEnabled = true
                                                    text = originalText
                                                }
                                                requireActivity().finish()
                                            }
                                    }
                                } else {
                                    snackbarDismiss(
                                        "Login Gagal. Silahkan cek kembali username dan password!",
                                        binding.root
                                    )
                                    binding.btnLoginGaskan.apply {
                                        icon = null
                                        isEnabled = true
                                        text = originalText
                                    }
                                }
                            }
                    }

                } else {
                    snackbarDismiss(
                        "Login Gagal. Silahkan cek kembali username dan password!", binding.root
                    )
                    binding.btnLoginGaskan.apply {
                        icon = null
                        isEnabled = true
                        text = originalText
                    }
                }
            }
        }
    }

    fun isInputFieldsLengkap(): Boolean {
        var lengkap = false
        loginInputsArray.forEach { input ->
            if (input.editText?.text.toString().trim().isEmpty() || input.editText?.text.toString()
                    .trim().isNullOrBlank()
            ) {
                input.error = "${input.hint} perlu diisi"

            } else {
                input.apply {
                    isErrorEnabled = false
                    error = null
                }
            }
        }
        val result = loginInputsArray.any { input ->
            input.editText?.text.toString().trim().isEmpty() || input.editText?.text.toString()
                .trim().isNullOrBlank()
        }
        return !result
    }

    data class Useer(
        val nama: String, val email: String, val username: String
    )

    fun getEmailFromDB(callback: (UserModel?) -> Unit) {
        firebaseDatabase.child("users").orderByChild("username").equalTo(loginUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(UserModel::class.java)
                        val email =
                            snapshot.children.first().child("email").getValue(String::class.java)!!
                        val nama = snapshot.children.first()
                        callback(user)

                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
}