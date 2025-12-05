package com.aquaspoof.unified.toolkit.mcpe

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import com.aquaspoof.unified.toolkit.mcpe.databinding.SetupActivityBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: SetupActivityBinding

    private var scriptsUriSet = false
    private var configsUriSet = false

    private val scriptsLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        saveUri(uri, "scripts_folder_uri", "scripts") {
            scriptsUriSet = true
            binding.buttonScripts.text = getString(R.string.setup_button_granted)
            binding.buttonScripts.isEnabled = false
            checkCompletion()
        }
    }

    private val configsLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        saveUri(uri, "configs_folder_uri", "configs") {
            configsUriSet = true
            binding.buttonConfigs.text = getString(R.string.setup_button_granted)
            binding.buttonConfigs.isEnabled = false
            checkCompletion()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.buttonNotify.text = getString(R.string.setup_button_granted)
            binding.buttonNotify.isEnabled = false
        } else {
            Toast.makeText(this, "Уведомления не разрешены", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(LocaleHelper.PREFS_NAME, Context.MODE_PRIVATE)
        val isSetupComplete = prefs.getBoolean("isSetupComplete", false)
        val hasSeenInfo = prefs.getBoolean("hasSeenInfoScreen", false)

        if (isSetupComplete && hasSeenInfo) {
            goToMainApp()
            return
        }

        if (isSetupComplete && !hasSeenInfo) {
            goToInfoApp()
            return
        }

        binding = SetupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (prefs.getString("scripts_folder_uri", null) != null) {
            scriptsUriSet = true
            binding.buttonScripts.text = getString(R.string.setup_button_granted)
            binding.buttonScripts.isEnabled = false
        }

        if (prefs.getString("configs_folder_uri", null) != null) {
            configsUriSet = true
            binding.buttonConfigs.text = getString(R.string.setup_button_granted)
            binding.buttonConfigs.isEnabled = false
        }

        checkNotificationPermission()
        checkCompletion()
        setupLanguageSelector()

        binding.buttonScripts.setOnClickListener {
            scriptsLauncher.launch(null)
        }

        binding.buttonConfigs.setOnClickListener {
            configsLauncher.launch(null)
        }

        binding.buttonNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Toast.makeText(this, "На Android 11 и ниже разрешение не требуется", Toast.LENGTH_LONG).show()
                binding.buttonNotify.text = getString(R.string.setup_button_granted)
                binding.buttonNotify.isEnabled = false
            }
        }

        binding.buttonFinish.setOnClickListener {
            prefs.edit {
                putBoolean("isSetupComplete", true)
            }
            goToInfoApp()
        }
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

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
                binding.buttonNotify.text = getString(R.string.setup_button_granted)
                binding.buttonNotify.isEnabled = false
            }
        } else {
            binding.buttonNotify.text = getString(R.string.setup_button_granted)
            binding.buttonNotify.isEnabled = false
        }
    }

    private fun checkCompletion() {
        if (scriptsUriSet && configsUriSet) {
            binding.buttonFinish.isEnabled = true
        }
    }

    private fun goToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToInfoApp() {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUri(uri: Uri?, key: String, expectedFolderName: String, onComplete: () -> Unit) {
        if (uri != null) {

            val folderName = getFolderNameFromUri(uri)
            if (folderName?.lowercase() != expectedFolderName.lowercase()) {
                Toast.makeText(
                    this,
                    getString(R.string.setup_error_wrong_folder, expectedFolderName),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                val prefs = getSharedPreferences(LocaleHelper.PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit {
                    putString(key, uri.toString())
                }

                onComplete()
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.setup_error_permission_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFolderNameFromUri(uri: Uri): String? {
        val documentFile = DocumentFile.fromTreeUri(this, uri)
        return documentFile?.name
    }
}