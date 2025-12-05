package com.aquaspoof.unified.toolkit.mcpe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.LicenseActivityBinding

class LicenseActivity : AppCompatActivity() {

    private lateinit var binding: LicenseActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LicenseActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.tvLicenseName.text = getString(R.string.license_name_mit)
        binding.tvLicenseText.text = getString(R.string.license_full_text_mit)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}