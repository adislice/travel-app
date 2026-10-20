package com.uty.travelersapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.uty.travelersapp.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val upArrow = androidx.core.content.ContextCompat.getDrawable(
            this,
            com.google.android.material.R.drawable.abc_ic_ab_back_material
        )
        upArrow?.setColorFilter(
            androidx.core.content.ContextCompat.getColor(
                this,
                R.color.blue_main
            ), android.graphics.PorterDuff.Mode.SRC_ATOP
        )
        binding.toolbarPrivacyPolicy.navigationIcon = upArrow
        binding.toolbarPrivacyPolicy.setNavigationOnClickListener {
            finish()
        }
    }
}
