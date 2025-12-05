package com.aquaspoof.unified.toolkit.mcpe

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.aquaspoof.unified.toolkit.mcpe.databinding.ScriptDetailActivityBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class ScriptDetailActivity : AppCompatActivity() {

    private lateinit var binding: ScriptDetailActivityBinding
    private var scriptItem: ScriptItem? = null
    private var downloadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScriptDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jsonString = intent.getStringExtra("script_json_data")
        if (jsonString != null) {
            try {
                scriptItem = Gson().fromJson(jsonString, ScriptItem::class.java)
            } catch (_: Exception) {
            }
        }

        if (scriptItem == null) {
            Toast.makeText(this, "Ошибка: данные скрипта не получены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        populateUi()

        binding.buttonDownload.setOnClickListener {
            if (downloadJob?.isActive == true) {
                downloadJob?.cancel()
                resetDownloadUI()
                checkFileStatus()
            } else {
                startDownload()
            }
        }

        binding.buttonCompare.setOnClickListener {
            compareHashes()
        }
    }

    private fun copyToClipboard(label: String, text: String?) {
        if (text.isNullOrEmpty() || text == getString(R.string.detail_not_specified)) {
            Toast.makeText(this, "Нечего копировать", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label скопирован: $text", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun startDownload() {
        val item = scriptItem ?: return

        binding.layoutProgressInfo.visibility = View.VISIBLE
        binding.buttonDownload.text = getString(R.string.download_cancel)
        binding.buttonDownload.setIconResource(android.R.drawable.ic_menu_close_clear_cancel)

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
        binding.buttonDownload.setTextColor(typedValue.data)

        downloadJob = lifecycleScope.launch {
            ScriptDownloader.download(this@ScriptDetailActivity, item).collect { state ->
                when (state) {
                    is DownloadState.Progress -> {
                        val downloadedStr = Formatter.formatShortFileSize(this@ScriptDetailActivity, state.downloadedBytes)
                        val totalStr = Formatter.formatShortFileSize(this@ScriptDetailActivity, state.totalBytes)
                        val speedStr = Formatter.formatShortFileSize(this@ScriptDetailActivity, state.speedBps)

                        binding.progressBar.progress = state.progress
                        binding.buttonDownload.text = "${state.progress}%"
                        binding.tvProgressSize.text = "$downloadedStr / $totalStr"
                        binding.tvProgressSpeed.text = speedStr
                    }
                    is DownloadState.Success -> {
                        resetDownloadUI()
                        checkFileStatus()
                        Toast.makeText(this@ScriptDetailActivity, getString(R.string.download_complete) + ": ${state.filename}", Toast.LENGTH_SHORT).show()
                    }
                    is DownloadState.Error -> {
                        resetDownloadUI()
                        checkFileStatus()
                        Toast.makeText(this@ScriptDetailActivity, "${getString(R.string.download_failed)}: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    is DownloadState.Idle -> {}
                }
            }
        }
    }

    private fun resetDownloadUI() {
        binding.layoutProgressInfo.visibility = View.GONE
        binding.progressBar.progress = 0
        binding.tvProgressSize.text = ""
        binding.tvProgressSpeed.text = ""
        binding.buttonDownload.icon = null

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
        binding.buttonDownload.setTextColor(typedValue.data)
    }

    override fun onResume() {
        super.onResume()
        if (downloadJob?.isActive != true) {
            resetDownloadUI()
            checkFileStatus()
        }
    }

    private fun checkFileStatus() {
        val item = scriptItem ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val prefs = getSharedPreferences(LocaleHelper.PREFS_NAME, Context.MODE_PRIVATE)
            val localVersion = prefs.getString("version_${item.id}", null)
            val serverVersion = item.version

            val folderUriString = prefs.getString("scripts_folder_uri", null)
            var fileExists = false
            var fileUri: Uri? = null

            if (folderUriString != null) {
                try {
                    val folderUri = folderUriString.toUri()
                    val scriptsFolder = DocumentFile.fromTreeUri(this@ScriptDetailActivity, folderUri)
                    if (scriptsFolder != null) {
                        val existingFile = scriptsFolder.findFile(item.filename)
                        if (existingFile != null && existingFile.exists()) {
                            fileExists = true
                            fileUri = existingFile.uri
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            withContext(Dispatchers.Main) {
                resetDownloadUI()
                updateButtonUI(fileExists, localVersion, serverVersion)
                if (fileExists && fileUri != null) {
                    binding.layoutLocalHashes.visibility = View.VISIBLE
                    calculateAndDisplayLocalHashes(fileUri)
                } else {
                    binding.layoutLocalHashes.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateAndDisplayLocalHashes(uri: Uri) {
        lifecycleScope.launch {
            try {
                val hashes = HashCalculator.calculateHashes(this@ScriptDetailActivity, uri)
                binding.tvLocalMd5.text = getString(R.string.detail_hash_md5, hashes.md5)
                binding.tvLocalSha1.text = getString(R.string.detail_hash_sha1, hashes.sha1)
                binding.tvLocalSha256.text = getString(R.string.detail_hash_sha256, hashes.sha256)
                binding.tvLocalCrc32.text = getString(R.string.detail_hash_crc32, hashes.crc32)

                binding.tvLocalMd5.setOnClickListener { copyToClipboard("Local MD5", hashes.md5) }
                binding.tvLocalSha1.setOnClickListener { copyToClipboard("Local SHA1", hashes.sha1) }
                binding.tvLocalSha256.setOnClickListener { copyToClipboard("Local SHA256", hashes.sha256) }
                binding.tvLocalCrc32.setOnClickListener { copyToClipboard("Local CRC32", hashes.crc32) }

                compareHashes()
            } catch (_: Exception) {
                binding.tvLocalMd5.text = "Error"
            }
        }
    }

    private fun compareHashes() {
        val item = scriptItem ?: return
        val md5Match = compareHashText(binding.tvServerMd5, binding.tvLocalMd5, item.md5)
        compareHashText(binding.tvServerSha1, binding.tvLocalSha1, item.sha1)
        compareHashText(binding.tvServerSha256, binding.tvLocalSha256, item.sha256)
        compareHashText(binding.tvServerCrc32, binding.tvLocalCrc32, item.crc32)

        if (md5Match) {
            binding.buttonDownload.text = getString(R.string.script_button_downloaded)
            binding.buttonDownload.isEnabled = false
        }
    }

    private fun compareHashText(serverView: TextView, localView: TextView, serverHash: String?): Boolean {
        val localHash = localView.text.toString().split(": ").getOrNull(1)

        if (serverHash == null || localHash == null) {
            serverView.setTextColor(Color.GRAY)
            localView.setTextColor(Color.GRAY)
            return false
        }

        if (serverHash.equals(localHash, ignoreCase = true)) {
            serverView.setTextColor(Color.GREEN)
            localView.setTextColor(Color.GREEN)
            return true
        } else {
            serverView.setTextColor(Color.RED)
            localView.setTextColor(Color.RED)
            return false
        }
    }

    private fun updateButtonUI(fileExists: Boolean, localVersion: String?, serverVersion: String) {
        binding.buttonDownload.isEnabled = true
        when {
            !fileExists -> {
                binding.buttonDownload.text = getString(R.string.script_button_download)
            }
            localVersion?.trim() != serverVersion.trim() -> {
                binding.buttonDownload.text = getString(R.string.script_button_update)
            }
            else -> {
                binding.buttonDownload.text = getString(R.string.script_button_downloaded)
                binding.buttonDownload.isEnabled = false
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = scriptItem?.name ?: getString(R.string.app_name)
    }

    private fun populateUi() {
        val item = scriptItem ?: return
        val na = getString(R.string.detail_not_specified)

        binding.tvDescription.text = item.description

        val authorsText = item.authors?.joinToString(", ")?.ifEmpty { na } ?: na
        binding.tvAuthor.text = getString(R.string.detail_label_author, authorsText)

        binding.tvVersion.text = getString(R.string.detail_label_version, item.version)
        binding.tvLicense.text = getString(R.string.detail_label_license, item.license)
        binding.tvFilename.text = getString(R.string.detail_label_filename, item.filename)
        binding.tvDownloads.text = getString(R.string.detail_label_downloads, item.downloadCount)

        val fileSizeStr = Formatter.formatShortFileSize(this, item.fileSize)
        binding.tvFilesize.text = getString(R.string.detail_label_filesize, fileSizeStr)

        binding.tvGithub.text = getString(R.string.detail_label_github, item.github ?: na)

        val websitesText = item.websites?.joinToString("\n")?.ifEmpty { na } ?: na
        binding.tvWebsites.text = getString(R.string.detail_label_websites, websitesText)

        binding.tvServerMd5.text = getString(R.string.detail_hash_md5, item.md5 ?: na)
        binding.tvServerSha1.text = getString(R.string.detail_hash_sha1, item.sha1 ?: na)
        binding.tvServerSha256.text = getString(R.string.detail_hash_sha256, item.sha256 ?: na)
        binding.tvServerCrc32.text = getString(R.string.detail_hash_crc32, item.crc32 ?: na)

        binding.tvServerMd5.setOnClickListener { copyToClipboard("MD5", item.md5) }
        binding.tvServerSha1.setOnClickListener { copyToClipboard("SHA1", item.sha1) }
        binding.tvServerSha256.setOnClickListener { copyToClipboard("SHA256", item.sha256) }
        binding.tvServerCrc32.setOnClickListener { copyToClipboard("CRC32", item.crc32) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}