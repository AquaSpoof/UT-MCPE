package com.aquaspoof.unified.toolkit.mcpe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class RegisterActivity : AppCompatActivity() {
// когда----------------то буду юзать его

    // а вы знали что если много знака "-" то этот код написал чат гпт :)
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonRegister.setOnClickListener {
            registerUser()
        }

        binding.buttonGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Пароли не совпадают"
            return
        } else {
            binding.tilConfirmPassword.error = null
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Пароль должен быть не менее 6 символов"
            return
        } else {
            binding.tilPassword.error = null
        }

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                    goToMainApp()
                } else {
                    handleAuthFailure(task.exception)
                }
            }
    }

    private fun goToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !isLoading
        binding.buttonGoToLogin.isEnabled = !isLoading
    }

    private fun handleAuthFailure(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthWeakPasswordException ->
                "Ошибка: Пароль слишком слабый. Используйте не менее 6 символов."
            is FirebaseAuthUserCollisionException ->
                "Ошибка: Пользователь с таким Email уже существует."
            is FirebaseAuthInvalidCredentialsException ->
                "Ошибка: Некорректный формат Email."
            else ->
                "Ошибка регистрации: ${exception?.message}"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}