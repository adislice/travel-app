package com.uty.travelersapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.uty.travelersapp.databinding.ActivityMainBinding
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        installSplashScreen()
        val currUser: FirebaseUser? = firebaseAuth.currentUser
        currUser?.let {
            if (currUser.isEmailVerified) {

                val i = Intent(this, DashboardActivity::class.java)
//                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i)
                finish()
                return
            } else {
                try {
                    firebaseAuth.signOut()
                } catch (e: FirebaseAuthException) {
                    Toast.makeText(this, "Exception: " + e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_main)

//        val windowInsetsController = window?.let { ViewCompat.getWindowInsetsController(it.decorView) }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
    }

    override fun onDestroy() {
        Log.d("kencana", "Clearing cache...")
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()
    }


}