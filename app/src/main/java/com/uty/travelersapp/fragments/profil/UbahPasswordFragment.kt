package com.uty.travelersapp.fragments.profil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.firebase.auth.EmailAuthProvider
import com.uty.travelersapp.R
import com.uty.travelersapp.customs.MyAlertDialogBuilder
import com.uty.travelersapp.customs.MyAlertDialogType
import com.uty.travelersapp.databinding.FragmentUbahPasswordBinding
import com.uty.travelersapp.utils.FirebaseUtils
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth
import com.uty.travelersapp.utils.MyUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

class UbahPasswordFragment : Fragment() {
    private var _binding: FragmentUbahPasswordBinding? = null
    private val binding get() = _binding!!
    private var oldPassword = MutableStateFlow("")
    private var password = MutableStateFlow("")
    private var passwordConfirm = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUbahPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.outline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        with(binding) {
            inputPassword.editText?.doOnTextChanged { text, start, before, count ->
                password.value = text.toString()
            }
            inputPasswordConfirm.editText?.doOnTextChanged { text, start, before, count ->
                passwordConfirm.value = text.toString()
            }
            inputPasswordLama.editText?.doOnTextChanged { text, start, before, count ->
                oldPassword.value = text.toString()
            }
        }

        lifecycleScope.launch {
            formIsValid.collect {
                binding.btnSimpanPassword.isEnabled = it
            }
        }

        binding.btnSimpanPassword.setOnClickListener {
            ubahPassword()
        }


    }

    private var formIsValid = combine(
        oldPassword,
        password,
        passwordConfirm,
    ) { oldPassword, password, passwordConfirm ->
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[0-9])(?=\\S+$).*$"
        val isPasswordEmpty = password.isEmpty()
        val isOldPassEmpty = oldPassword.isEmpty()
        val isPasswordCEmpty = passwordConfirm.isEmpty()
        var passIdentical = false

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        if (password.length >= 8 && matcher.matches()) {
            binding.inputPassword.error = null
            binding.inputPassword.isErrorEnabled = false
        } else {
            binding.inputPassword.error =
                "Password harus memiliki kombinasi huruf dan angka serta, minimal 8 karakter, dan tanpa spasi"
            binding.inputPassword.isErrorEnabled = true
        }

        if (isPasswordCEmpty) {
            binding.inputPasswordConfirm.error = "Konfirmasi password tidak boleh kosong"
            binding.inputPasswordConfirm.isErrorEnabled = true
        } else {
            binding.inputPasswordConfirm.error = null
            binding.inputPasswordConfirm.isErrorEnabled = false
        }

        if (isOldPassEmpty) {
            binding.inputPasswordLama.error = "Anda perlu memasukkan password lama untuk mengubah password"
            binding.inputPasswordLama.isErrorEnabled = true
        } else {
            binding.inputPasswordLama.error = null
            binding.inputPasswordLama.isErrorEnabled = false
        }

        if (password.trim() == passwordConfirm.trim()) {
            passIdentical = true
        }
        if (passIdentical) {
            binding.inputPasswordConfirm.error = null
            binding.inputPasswordConfirm.isErrorEnabled = false
        } else {
            binding.inputPasswordConfirm.error = "Konfirmasi password tidak cocok"
            binding.inputPasswordConfirm.isErrorEnabled = true
        }

        !isPasswordEmpty and !isOldPassEmpty and !isPasswordCEmpty and passIdentical

    }

    fun ubahPassword() {

        val spec = CircularProgressIndicatorSpec(
            requireActivity(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        val progressIndicatorDrawable =
            IndeterminateDrawable.createCircularDrawable(requireActivity(), spec)
        binding.btnSimpanPassword.apply {
            isEnabled = false
            icon = progressIndicatorDrawable

        }

        val credential = EmailAuthProvider.getCredential(MyUser.user?.email!!, oldPassword.value)
        firebaseAuth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePassword(password.value)
            } else {
                MyAlertDialogBuilder(requireActivity(), MyAlertDialogType.ERROR).apply {
                    setTitle("Gagal!")
                    setBody("Password gagal diubah. Password lama yang Anda masukkan salah!")
                    setConfirmButton("Oke") {
                        close()
                    }
                }.show()
            }
        }

    }

    private fun updatePassword(newPass: String) {
        firebaseAuth.currentUser?.updatePassword(newPass)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                MyAlertDialogBuilder(requireActivity(), MyAlertDialogType.SUCCESS).apply {
                    setCancelable(false)
                    setTitle("Sukses!")
                    setBody("Password berhasil diubah!")
                    setConfirmButton("Oke") {
                        close()
                        findNavController().popBackStack()
                    }
                }.show()
            } else {
                MyAlertDialogBuilder(requireActivity(), MyAlertDialogType.ERROR).apply {
                    setTitle("Gagal!")
                    setBody("Password gagal diubah. Silahkan ulangi beberapa saat lagi!")
                    setConfirmButton("Oke") {
                        close()
                    }
                }.show()
                Log.d("kencana", "ubah pass: " + task.exception?.message)
            }
            binding.btnSimpanPassword.apply {
                isEnabled = true
                icon = null
            }

        }
    }


}