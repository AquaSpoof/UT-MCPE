package com.aquaspoof.unified.toolkit.mcpe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aquaspoof.unified.toolkit.mcpe.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
// когда----------------то буду юзать его

    // а вы знали что если много знака "-" то этот код написал чат гпт :)
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)

        binding.buttonLogin.setOnClickListener {
            signInUser()
        }

        binding.buttonGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.buttonForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Введите Email для сброса пароля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            resetPassword(email)
        }
    }

    private fun signInUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email и Пароль не должны быть пустыми", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    goToMainApp()
                } else {
                    handleAuthFailure(task.exception)
                }
            }
    }

    private fun resetPassword(email: String) {
        setLoading(true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Письмо для сброса пароля отправлено на $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_LONG).show()
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
        binding.buttonLogin.isEnabled = !isLoading
        binding.buttonGoToRegister.isEnabled = !isLoading
    }

    private fun handleAuthFailure(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthInvalidCredentialsException ->
                "Ошибка: Неверный пароль или email."
            is FirebaseAuthInvalidUserException ->
                "Ошибка: Пользователь с таким Email не найден или отключен."
            else ->
                "Ошибка аутентификации: ${exception?.message}"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}