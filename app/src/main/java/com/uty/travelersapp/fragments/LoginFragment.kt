package com.uty.travelersapp.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.toObject
import com.uty.travelersapp.DashboardActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentLoginBinding
import com.uty.travelersapp.extensions.Helpers.Companion.saveUserPref
import com.uty.travelersapp.extensions.Helpers.Companion.snackbarDismiss
import com.uty.travelersapp.fragments.paket_wisata.CheckoutFragment
import com.uty.travelersapp.models.UserModel
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import com.uty.travelersapp.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginEmail: String
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

        loginInputsArray = arrayOf(binding.inputLoginEmail, binding.inputLoginPassword)

        binding.btnLoginGaskan.setOnClickListener {
            login()
        }

        binding.btnResetPassword.setOnClickListener {
            showResetDialog()
        }
    }

    fun login() {
        loginEmail = binding.inputLoginEmail.editText?.text.toString()
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

            firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
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
                            setEmailVerified(task.result.user!!.uid, true)
                            var userDb:UserModel? = null
                            runBlocking {
                                var result = getUserDetail()
                                userDb = result
                            }
                            Snackbar.make(
                                binding.btnLoginGaskan,
                                "Login berhasil " + task.result.user?.email,
                                Snackbar.LENGTH_LONG
                            ).show()
//                            val profileUpdate = userProfileChangeRequest {
//                                displayName = userDb!!.nama?.trim()
//                            }
                            val intent = Intent(requireActivity(), DashboardActivity::class.java)
                            startActivity(intent)
                            requireContext().saveUserPref(userDb!!)
                            binding.btnLoginGaskan.apply {
                                icon = null
                                isEnabled = true
                                text = originalText
                            }
                            requireActivity().finish()
//                            task.result.user!!.updateProfile(profileUpdate)
//                                .addOnCompleteListener { updateProfileTask ->
//                                    if (updateProfileTask.isSuccessful) {
//                                        Log.d("firebase", "User profile updated")
//                                    }
//                                    val intent = Intent(
//                                        requireActivity(),
//                                        DashboardActivity::class.java
//                                    )
////                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//                                }
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

    suspend fun getUserDetail(): UserModel? {
        val user =
            firebaseDatabase.collection("users").whereEqualTo("email", loginEmail).get()
                .await()
        return if (user.documents.isNotEmpty()) {
            user.documents.first().toObject<UserModel>()
        } else {
            null
        }
    }

    fun setEmailVerified(uid: String, verified: Boolean) {
        firebaseDatabase.collection("users").document(uid).update(
            mapOf(
                "verified" to verified
            )
        )
    }

    private fun showResetDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Reset Password")

        val alertLayout = layoutInflater.inflate(R.layout.dialog_input_email_reset, null)
        builder.setView(alertLayout)
//        alertLayout.findViewById<TextInputLayout>(R.id.input_email_reset).editText?.setText(promoTerpasang.kode)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            val enteredEmail = alertLayout.findViewById<TextInputLayout>(R.id.input_email_reset).editText?.text.toString()
            sendPasswordReset(enteredEmail, dialog)

        }

        builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        builder.create().show()


    }

    private fun sendPasswordReset(emailR: String, dialog: DialogInterface) {
        firebaseAuth.sendPasswordResetEmail(emailR).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Email Reset Password Terkirim")
                    .setMessage("Kami telah mengirimkan email berisi tautan untuk reset password ke email Anda (${emailR}). Silahkan klik tautan tersebut untuk mereset password akun Anda.")
                    .setPositiveButton("Baik") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Email Reset Password Gagal Dikirim")
                    .setMessage("Silahkan cek kembali email Anda (${emailR}) apakah sudah benar dan sudah pernah mendaftar di aplikasi Kencana Wisata.")
                    .setPositiveButton("Baik") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            dialog.dismiss()
        }
    }

}