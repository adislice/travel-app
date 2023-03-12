package com.uty.travelersapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.uty.travelersapp.databinding.ActivityMainBinding
import com.uty.travelersapp.utils.FirebaseUtils.firebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_main)

//        val windowInsetsController = window?.let { ViewCompat.getWindowInsetsController(it.decorView) }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
    }

    override fun onStart() {
        super.onStart()
        val currUser: FirebaseUser? = firebaseAuth.currentUser
        currUser?.let {
            if (currUser.isEmailVerified) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                try {
                    firebaseAuth.signOut()
                } catch (e: FirebaseAuthException) {
                    Toast.makeText(this, "Exception: " + e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}