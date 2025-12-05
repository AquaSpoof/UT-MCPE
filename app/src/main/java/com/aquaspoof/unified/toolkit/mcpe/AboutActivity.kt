package com.aquaspoof.unified.toolkit.mcpe
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.AboutActivityBinding
import com.aquaspoof.unified.toolkit.mcpe.BuildConfig

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.tvAppName.text = getString(
            R.string.about_label_app_name,
            getString(R.string.app_name)
        )

        binding.tvVersion.text = getString(
            R.string.about_label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE.toString()
        )

        binding.tvPackage.text = getString(
            R.string.about_label_package,
            BuildConfig.APPLICATION_ID
        )

        binding.tvAuthor.text = getString(
            R.string.about_label_author,
            getString(R.string.about_author_name)
        )

        binding.tvGithub.text = getString(
            R.string.about_label_github,
            getString(R.string.about_github_url)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}