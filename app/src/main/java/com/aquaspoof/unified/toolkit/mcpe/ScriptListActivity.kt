package com.aquaspoof.unified.toolkit.mcpe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aquaspoof.unified.toolkit.mcpe.databinding.ScriptListActivityBinding
import kotlinx.coroutines.launch

class ScriptListActivity : AppCompatActivity() {

    private lateinit var binding: ScriptListActivityBinding
    private lateinit var scriptAdapter: ScriptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScriptListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupRecyclerViewAndLoadData()
    }

    private fun setupRecyclerViewAndLoadData() {
        scriptAdapter = ScriptAdapter(emptyList())
        binding.recyclerViewScripts.apply {
            adapter = scriptAdapter
            layoutManager = LinearLayoutManager(this@ScriptListActivity)
        }

        lifecycleScope.launch {
            try {
                val scripts = ApiService.instance.getScripts()
                scriptAdapter.updateData(scripts)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ScriptListActivity, "Ошибка загрузки списка скриптов", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}