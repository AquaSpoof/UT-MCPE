package com.aquaspoof.unified.toolkit.mcpe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aquaspoof.unified.toolkit.mcpe.databinding.ActivityConfigListBinding
import kotlinx.coroutines.launch

class ConfigListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigListBinding
    private lateinit var configAdapter: ConfigAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Configs"

        setupRecyclerViewAndLoadData()
    }

    private fun setupRecyclerViewAndLoadData() {
        configAdapter = ConfigAdapter(emptyList())
        binding.recyclerViewConfigs.apply {
            adapter = configAdapter
            layoutManager = LinearLayoutManager(this@ConfigListActivity)
        }

        lifecycleScope.launch {
            try {
                val configs = ApiService.instance.getConfigs()
                configAdapter.updateData(configs)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ConfigListActivity, "Ошибка загрузки конфигов (API?)", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}