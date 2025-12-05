package com.aquaspoof.unified.toolkit.mcpe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.aquaspoof.unified.toolkit.mcpe.databinding.InfoActivityBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: InfoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("hasSeenInfoScreen", false)) {
            goToMainApp()
            return
        }

        binding = InfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonContinue.setOnClickListener {
            prefs.edit {
                putBoolean("hasSeenInfoScreen", true)
            }
            goToMainApp()
        }
    }

    private fun goToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}