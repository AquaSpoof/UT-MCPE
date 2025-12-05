package com.aquaspoof.unified.toolkit.mcpe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.CategoryActivityBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: CategoryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CategoryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.buttonScripts.setOnClickListener {
            val intent = Intent(this, ScriptListActivity::class.java)
            startActivity(intent)
        }
        binding.buttonConfigs.setOnClickListener {
            val intent = Intent(this, ConfigListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}