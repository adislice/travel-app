package com.uty.travelersapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.uty.travelersapp.databinding.ActivityDashboardBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.utils.FirebaseUtils

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        window.decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val bottomNavView = binding.bottomNavView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navitem_home,
                R.id.navitem_tempat_wisata,
                R.id.navitem_paket_wisata,
                R.id.navitem_profil
            )
        )
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        navController.addOnDestinationChangedListener{ controller: NavController, destination:NavDestination, bundle: Bundle? ->
            bottomNavView.isVisible = appBarConfiguration.topLevelDestinations.contains(destination.id)
        }
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