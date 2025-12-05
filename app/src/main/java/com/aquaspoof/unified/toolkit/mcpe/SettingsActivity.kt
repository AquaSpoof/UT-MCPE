package com.aquaspoof.unified.toolkit.mcpe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupLanguageSelector()
    }

    private fun setupLanguageSelector() {
        val currentLang = LocaleHelper.getSavedLanguage(this)

        val systemLang = java.util.Locale.getDefault().displayLanguage
        binding.rbSystem.text = getString(R.string.setup_lang_system_dynamic, systemLang)

        when (currentLang) {
            LocaleHelper.LANG_RUSSIAN -> binding.rgLanguage.check(R.id.rb_russian)
            LocaleHelper.LANG_ENGLISH -> binding.rgLanguage.check(R.id.rb_english)
            LocaleHelper.LANG_UKRAINIAN -> binding.rgLanguage.check(R.id.rb_ukrainian)
            LocaleHelper.LANG_BELARUSIAN -> binding.rgLanguage.check(R.id.rb_belarusian)
            LocaleHelper.LANG_CHINESE_S -> binding.rgLanguage.check(R.id.rb_chinese_s)
            LocaleHelper.LANG_CHINESE_T -> binding.rgLanguage.check(R.id.rb_chinese_t)
            else -> binding.rgLanguage.check(R.id.rb_system)
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val lang = when (checkedId) {
                R.id.rb_russian -> LocaleHelper.LANG_RUSSIAN
                R.id.rb_english -> LocaleHelper.LANG_ENGLISH
                R.id.rb_ukrainian -> LocaleHelper.LANG_UKRAINIAN
                R.id.rb_belarusian -> LocaleHelper.LANG_BELARUSIAN
                R.id.rb_chinese_s -> LocaleHelper.LANG_CHINESE_S
                R.id.rb_chinese_t -> LocaleHelper.LANG_CHINESE_T
                else -> LocaleHelper.LANG_SYSTEM
            }
            LocaleHelper.saveLocalePreference(this, lang)
            LocaleHelper.setLocale(lang)
            recreate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}