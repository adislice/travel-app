package com.uty.travelersapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.uty.travelersapp.models.PaketWisataItem

class PaketWisataBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paket_wisata_base)
//        val detailPaketWisata = intent.getSerializableExtra("DETAIL_PAKETWISATA") as PaketWisataItem
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = true
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}