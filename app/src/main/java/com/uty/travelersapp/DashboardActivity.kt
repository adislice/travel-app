package com.uty.travelersapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.uty.travelersapp.databinding.ActivityDashboardBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.FirebaseUtils
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.UserViewModel

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoggedUser()

//        window.decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = true

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

    fun checkLoggedUser() {
        val currUser: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
        currUser?.let {
            if (currUser.isEmailVerified) {
                userViewModel.setUser(currUser.uid)
                userViewModel.userDetailData.observe(this) { response ->
                    when (response) {
                        is Response.Success -> {
                            MyUser.user = response.data
                        }
                        is Response.Failure -> {
                            FirebaseUtils.firebaseAuth.signOut()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {}
                    }
                }
                return
            } else {
                try {
                    FirebaseUtils.firebaseAuth.signOut()
                } catch (e: FirebaseAuthException) {
                    Toast.makeText(this, "Exception: " + e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

}