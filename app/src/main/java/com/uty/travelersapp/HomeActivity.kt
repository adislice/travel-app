package com.uty.travelersapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uty.travelersapp.databinding.ActivityHomeBinding
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val currUser = firebaseAuth.currentUser!!
        Snackbar.make(binding.root, "Anda login sebagai " + currUser.email, Snackbar.LENGTH_LONG)
            .show()

    }
}