package com.uty.travelersapp

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.uty.travelersapp.databinding.ActivityDetailTempatWisataBinding
import com.uty.travelersapp.models.TempatWisataModel


class DetailTempatWisataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTempatWisataBinding
    private var detailTempatWisata: TempatWisataModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTempatWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_detailwisata)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var typedVal = TypedValue()
        this.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant, typedVal, true
        )
        val col = typedVal.data
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout_detailwisata)
        val collapsedColor = ContextCompat.getDrawable(
            applicationContext, com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        collapsedColor?.setColorFilter(col, PorterDuff.Mode.SRC_ATOP)
        val expandedColor = ContextCompat.getDrawable(
            applicationContext, com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        expandedColor?.setColorFilter(
            ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon = expandedColor
        val expandedTitle = ""
        var isShow = true
        var scrollRange = -1

        appBarLayout.addOnOffsetChangedListener { barLayout, verticalOffset ->
//            val colo = toolbar.background as Drawable
//            txtCount.text = (colo as ColorDrawable).color.toString()
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset <= 90) {
                isShow = true
                toolbar.navigationIcon = collapsedColor
                windowInsetsController.isAppearanceLightStatusBars = true
            } else if (isShow) {
                windowInsetsController.isAppearanceLightStatusBars = false
                toolbar.navigationIcon = expandedColor
                isShow = false
            }
        }

        detailTempatWisata = intent.getSerializableExtra("DETAIL_TEMPATWISATA") as TempatWisataModel

        detailTempatWisata?.let {
            binding.txtDetailWisataNama.text = it.nama
            binding.txtDetailWisataKota.text = "Sleman" + ","
            binding.txtDetailWisataProvinsi.text = "Yogyakarta"
            binding.txtDetailWisataDeskripsi.text = it.deskripsi
            binding.collapsingToolbarLayout.title = it.nama
            Glide.with(this).load(it.gambar).centerCrop().placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder).into(binding.imgDetailWisata)
        }
    }
}