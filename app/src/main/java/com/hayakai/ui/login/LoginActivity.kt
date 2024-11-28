package com.hayakai.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.hayakai.R
import com.hayakai.data.pref.SessionModel
import com.hayakai.databinding.ActivityLoginBinding
import com.hayakai.navigation.BottomNavigation
import com.hayakai.ui.createaccount.CreateAccountActivity
import com.hayakai.ui.forgotpassword.ForgotPasswordActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)
        binding.btnForgotPassword.setOnClickListener(this)

        binding.etEmail.addTextChangedListener {
            validateEmail()
        }

        binding.etPassword.addTextChangedListener {
            validatePassword()
        }
    }

    private fun validateEmail(): Boolean {
        return when {
            binding.etEmail.text.toString().isEmpty() -> {
                binding.tilEmail.error = getString(R.string.ed_email_error_msg_is_empty)
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches() -> {
                binding.tilEmail.error = getString(R.string.ed_email_error_msg_is_invalid)
                false
            }

            else -> {
                binding.tilEmail.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        return when {
            binding.etPassword.text.toString().isEmpty() -> {
                binding.tilPassword.error = getString(R.string.ed_password_error_msg_is_empty)
                false
            }

            binding.etPassword.text.toString().length < 8 -> {
                binding.tilPassword.error = getString(R.string.ed_password_error_msg_invalid_length)
                false
            }

            else -> {
                binding.tilPassword.error = null
                true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                if (!validateEmail() or !validatePassword()) return

                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is MyResult.Loading -> {
                            binding.btnLogin.isEnabled = false
                        }

                        is MyResult.Success -> {
                            viewModel.saveSession(SessionModel(result.data.data.accessToken))
                            binding.btnLogin.isEnabled = true
                            val intent = Intent(this@LoginActivity, BottomNavigation::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        is MyResult.Error -> {
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            binding.btnLogin.isEnabled = true
                        }
                    }
                }
            }

            R.id.register_button -> {
                val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_forgot_password -> {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

            R.id.back_button -> {
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

}