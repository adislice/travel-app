package com.uty.travelersapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.core.view.*
import com.uty.travelersapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)



//        val windowInsetsController = window?.let { ViewCompat.getWindowInsetsController(it.decorView) }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.isAppearanceLightStatusBars = true
        windowInsetsController?.isAppearanceLightNavigationBars = true





    }
}