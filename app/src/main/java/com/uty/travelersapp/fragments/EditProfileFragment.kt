package com.uty.travelersapp.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.uty.travelersapp.R
import com.uty.travelersapp.customs.MyAlertDialogBuilder
import com.uty.travelersapp.customs.MyAlertDialogType
import com.uty.travelersapp.databinding.FragmentEditProfileBinding
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.FirebaseUtils
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 0
    private val REQUEST_PICK_IMAGE = -1
    private lateinit var cameraOutputUri: Uri
    private var profilePictureUri: Uri = Uri.EMPTY
    private var name = MutableStateFlow("")
    private var email = MutableStateFlow("")
    private var noTelp = MutableStateFlow("")
    lateinit var currentPhotoPath: String
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarEditprofile
        val btnGantiFoto = binding.btnGantiFotoProfil
        btnGantiFoto.setOnClickListener {
            checkAndRequestPermission()
            val file: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM)
            cameraOutputUri = Uri.fromFile(file)
            val intent = getPickIntent(cameraOutputUri)
            startActivityForResult(intent, -1)
        }

        with(binding) {
            inputEditNama.editText?.doOnTextChanged { text, start, before, count ->
                name.value = text.toString()
            }
            inputEditEmail.editText?.doOnTextChanged { text, start, before, count ->
                email.value = text.toString()
            }
            inputEditNoTelp.editText?.doOnTextChanged { text, start, before, count ->
                noTelp.value = text.toString()
            }
        }

        lifecycleScope.launch {
            formIsValid.collect {
                binding.btnSimpanProfil.isEnabled = it
            }
        }


        Log.d("kencana", userViewModel.userDetailData.value.toString())
        userViewModel.userDetailData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Success -> {
                    binding.inputEditNama.editText?.setText(response.data.nama!!)
                    binding.inputEditEmail.editText?.setText(response.data.email!!)
                    response.data.no_telp?.let {
                        binding.inputEditNoTelp.editText?.setText(it)
                    }
                    response.data.profile_picture?.let{
                        Glide.with(requireContext())
                            .load(it)
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(binding.imgEditProfil)
                        binding.imgEditProfil.setColorFilter(null)
                        binding.imgEditProfil.imageTintList = null
                    }

                    Log.d("kencana", "ada " + name.value)
                }

                else -> {}
            }
        }

        val spec = CircularProgressIndicatorSpec(
            requireActivity(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        val progressIndicatorDrawable =
            IndeterminateDrawable.createCircularDrawable(requireActivity(), spec)
        
        binding.btnSimpanProfil.setOnClickListener {
            val userId = FirebaseUtils.firebaseAuth.currentUser!!.uid
            val fileExt = Helper.getMimeType(requireContext(), profilePictureUri)
            val fileName = "user_${userId}.${fileExt}"
            Log.d("kencana", "simpan profile")

            userViewModel.updateUser(userId, name.value, email.value, noTelp.value, profilePictureUri, fileName).observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.btnSimpanProfil.apply {
                            isEnabled = false
                            icon = progressIndicatorDrawable

                        }
                    }
                    is Response.Success -> {
                        Log.d("kencana", "update user status: " + response.data.toString())
                        userViewModel.setUser("kosong")
                        MyAlertDialogBuilder(requireActivity(), MyAlertDialogType.SUCCESS).apply {
                            setCancelable(false)
                            setTitle("Sukses!")
                            setBody("Profil berhasil diubah!")
                            setConfirmButton("Oke") {
                                close()
//                                userViewModel.refresh()

                                findNavController().popBackStack()


                            }
                        }.show()



                        binding.btnSimpanProfil.apply {
                            isEnabled = true
                            icon = null
                        }
                    }
                    is Response.Failure -> {
                        MyAlertDialogBuilder(requireActivity(), MyAlertDialogType.ERROR).apply {
                            setTitle("Gagal!")
                            setBody("Profil gagal diubah. Silahkan ulangi beberapa saat lagi!")
                            setConfirmButton("Oke") {
                                close()
                            }
                        }.show()
                        binding.btnSimpanProfil.apply {
                            isEnabled = true
                            icon = null
                        }
                    }

                    else -> {}
                }
            }


        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }

    fun getPickIntent(cameraOutputUri: Uri): Intent? {
        val intents = arrayListOf<Intent>()
        if (true) {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            intents.add(i)
        }
        if (true) {
            setCameraIntents(intents, cameraOutputUri)
        }

        if (intents.isEmpty()) return null
        val result = Intent.createChooser(intents.removeAt(0), null)
        if (!intents.isEmpty()) {
            result.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(arrayOf<Parcelable>()))
        }
        return result
    }

    fun setCameraIntents(intentArray:ArrayList<Intent>, output: Uri) {

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = requireActivity().packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        val outFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "userpic_" + System.currentTimeMillis().toString() + ".jpg")
        profilePictureUri = Uri.fromFile(outFile)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.d("kencana", "error membuat file")
            null
        }
        val photoURI: Uri = FileProvider.getUriForFile(
            requireContext(),
            "com.uty.travelersapp.fileprovider",
            photoFile!!
        )
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intentArray.add(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("kencana", "request code: " + requestCode.toString()+", result code: "+ resultCode.toString())
            val bitmap = data?.data

            val b = data?.extras?.get("data") as Bitmap?
            val u = data?.data
            Log.d("kencana", "res: "+b?.height.toString())
            currentPhotoPath?.let {
//            val camUri = Uri.fromFile(File(currentPhotoPath))
                binding.imgEditProfil.setImageURI(profilePictureUri)
                binding.imgEditProfil.setColorFilter(null)
                binding.imgEditProfil.imageTintList = null
                Log.d("kencana", "camera uri: " + currentPhotoPath)
                binding.imgEditProfil.setContentPadding(0, 0, 0, 0)
            }

            u?.let {
                profilePictureUri = it
                binding.imgEditProfil.setImageURI(it)
                binding.imgEditProfil.setColorFilter(null)
                binding.imgEditProfil.imageTintList = null
                binding.imgEditProfil.setContentPadding(0, 0, 0, 0)
            }
        }

//        binding.imgEditProfil.setImageBitmap(thumb)
//        binding.imgEditProfil.setImageURI(cameraOutputUri)
    }

    private var formIsValid = combine(
        name,
        email,
        noTelp
    ) { name, email, noTelp ->
        val isNameValid = name.isNotEmpty()
        val isEmailValid = email.length > 0
        val isNoTelpValid = noTelp.length > 0

        if (isNameValid) {
            binding.inputEditNama.error = null
            binding.inputEditNama.isErrorEnabled = false
        } else {
            binding.inputEditNama.error = "Nama tidak boleh kosong"
            binding.inputEditNama.isErrorEnabled = true
        }
        if (isEmailValid) {
            binding.inputEditEmail.error= null
            binding.inputEditEmail.isErrorEnabled = false
        } else {
            binding.inputEditEmail.error= "Email tidak boleh kosong"
            binding.inputEditEmail.isErrorEnabled = true
        }
        if (isNoTelpValid) {
            binding.inputEditNoTelp.error = null
            binding.inputEditNoTelp.isErrorEnabled = false
        } else {
            binding.inputEditNoTelp.error = "Nomor telepon tidak boleh kosong"
            binding.inputEditNoTelp.isErrorEnabled = true
        }


//        when {
//            isNameValid -> {
//                binding.inputEditNama.error = null
//                binding.inputEditNama.isErrorEnabled = false
//            }
//            isPasswordValid -> {
//                binding.inputEditPassword.error= null
//                binding.inputEditPassword.isErrorEnabled = false
//            }
//        }

        isNameValid and isEmailValid and isNoTelpValid

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            profilePictureUri = Uri.fromFile(this)
        }
    }
}