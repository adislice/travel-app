package com.uty.travelersapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.uty.travelersapp.databinding.ActivityDashboardBinding
import com.uty.travelersapp.utils.FirebaseUtils

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val bottomNavView = binding.bottomNavView
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        bottomNavView.setupWithNavController(navController)

        val currUser = FirebaseUtils.firebaseAuth.currentUser!!
        val snackbr = Snackbar.make(
            binding.root,
            "Anda login sebagai " + currUser.email,
            Snackbar.LENGTH_LONG
        )
        snackbr.anchorView = bottomNavView
        snackbr.isAnchorViewLayoutListenerEnabled = true
        snackbr.show()
    }
}