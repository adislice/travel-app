package com.uty.travelersapp.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firestore.v1.DocumentTransform.FieldTransform.ServerValue
import com.uty.travelersapp.R
import com.uty.travelersapp.databinding.FragmentRegisterBinding
import com.uty.travelersapp.models.UserModel
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.FirebaseUtils.firebaseDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var userName: String
    private lateinit var userUsername: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var createAccountInputsArray: Array<TextInputLayout>
    private lateinit var originalText: CharSequence

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAccountInputsArray = arrayOf(
            binding.inputNama,
            binding.inputUsername,
            binding.inputEmail,
            binding.inputPassword,
            binding.inputPasswordConfirm
        )

        val upArrow = ContextCompat.getDrawable(
            requireActivity(),
            com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        upArrow?.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.blue_main),
            PorterDuff.Mode.SRC_ATOP
        )

        binding.toolbarRegisterFragment.navigationIcon = upArrow
        binding.toolbarRegisterFragment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegisterGaskan.setOnClickListener {
            createAccount()
        }
    }

    fun createAccount() {
        val spec =
            CircularProgressIndicatorSpec(
                requireActivity(),  /*attrs=*/null, 0,
                com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
            )
        val progressIndicatorDrawable =
            IndeterminateDrawable.createCircularDrawable(requireActivity(), spec)
        originalText = binding.btnRegisterGaskan.text
        if (isInputFieldsLengkap()) {
            if (isEmailValid()) {
                if (isPasswordValid()) {
                    if (isPasswordIndentical()) {
                        binding.btnRegisterGaskan.apply {
                            icon = progressIndicatorDrawable
                            isEnabled = false
                            text = "Membuat akun..."
                        }
                        userEmail = binding.inputEmail.editText?.text.toString()
                        userPassword = binding.inputPassword.editText?.text.toString()
                        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
//                                    Toast.makeText(requireContext(), "sukses : ", Toast.LENGTH_LONG).show()
                                    val email = task.result.user?.email
                                    userName = binding.inputNama.editText?.text.toString().trim()

                                    val profileUpdate = userProfileChangeRequest {
                                        displayName = userName
                                    }
                                    task.result.user!!.updateProfile(profileUpdate)
                                        .addOnCompleteListener { updateProfileTask ->
                                            if (updateProfileTask.isSuccessful) {
                                                Log.d("firebase", "User profile updated")
                                            }
                                            sendEmailVerification(task.result.user!!)
                                        }

                                } else {
                                    val msg = (task.exception as FirebaseAuthException).errorCode
                                    Toast.makeText(
                                        requireContext(),
                                        "Error : " + msg,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    when (msg) {
                                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                            Snackbar.make(
                                                binding.btnRegisterGaskan,
                                                "Email sudah terdaftar",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    enableRegisterButton()
                                }
                            }

                    }
                }
            }
        }


    }

    fun isPasswordIndentical(): Boolean {
        if (binding.inputPassword.editText?.text.toString()
                .trim() == binding.inputPasswordConfirm.editText?.text.toString().trim()
        ) {
            binding.inputPasswordConfirm.apply {
                isErrorEnabled = false
                error = null
            }
            return true
        }
        binding.inputPasswordConfirm.error = "Konfirmasi Password tidak sama"
        return false
    }

    fun isInputFieldsLengkap(): Boolean {
        var lengkap = false
        createAccountInputsArray.forEach { input ->
            if (input.editText?.text.toString().trim().isEmpty()) {
                input.error = "${input.hint} perlu diisi"
            } else {
                input.apply {
                    isErrorEnabled = false
                    error = null
                }

            }
        }
        lengkap = createAccountInputsArray.any { input ->
            input.editText?.text.toString().trim().isEmpty()
        }
        return !lengkap
    }

    fun isEmailValid(): Boolean {
        val inputEmail = binding.inputEmail
        val email = inputEmail.editText?.text.toString()
        val pattern = Patterns.EMAIL_ADDRESS
        if (pattern.matcher(email).matches()) {
            inputEmail.apply {
                isErrorEnabled = false
                error = null
            }
            return true
        }
        inputEmail.error = "Email tidak valid"
        return false
    }


    fun isPasswordValid(): Boolean {
        val inputPass = binding.inputPassword
        val password = inputPass.editText?.text.toString()
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[0-9])(?=\\S+$).*$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        if (password.length >= 8 && matcher.matches()) {
            inputPass.apply {
                isErrorEnabled = false
                error = null
            }
            return true
        }
        inputPass.error =
            "Password harus memiliki kombinasi huruf dan angka serta, minimal 8 karakter, dan tanpa spasi"
        return false
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addAccountToDB(user)
            } else {
                Snackbar.make(
                    binding.btnRegisterGaskan,
                    "Email Verifikasi Gagal Dikirim",
                    Snackbar.LENGTH_LONG
                ).show()
                enableRegisterButton()
            }
        }

    }

    fun enableRegisterButton() {
        binding.btnRegisterGaskan.apply {
            icon = null
            isEnabled = true
            text = originalText
        }
    }


    fun addAccountToDB(registeredUser: FirebaseUser) {
//        Toast.makeText(requireContext(), "add to db ", Toast.LENGTH_LONG).show()

        val email = registeredUser.email
        val uid = registeredUser.uid
//        val userModel = UserModel(userName, email!!, userUsername)
        val userModel = mapOf(
            "nama" to userName,
            "email" to email!!,
            "role" to "customer",
            "created_at" to FieldValue.serverTimestamp()
        )
        firebaseDatabase.collection("users").document(uid).set(userModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Email Verifikasi Terkirim")
                        .setMessage("Kami telah mengirimkan email berisi tautan ke email Anda (${email}). Silahkan klik tautan tersebut untuk memverifikasi akun anda. Setelah akun Anda terverifikasi, Anda dapat Login untuk melanjutkan.")
                        .setPositiveButton("Baik") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
                enableRegisterButton()
            }

    }

    interface AddAccountCallback {
        var isSuccess: Boolean
    }


}