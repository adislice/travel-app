package com.uty.travelersapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.google.android.material.appbar.MaterialToolbar

class PemesananPaketWisataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemesanan_paket_wisata)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_pemesanan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
        finish()
        return true
    }
}