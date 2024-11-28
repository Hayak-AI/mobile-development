package com.hayakai.ui.createaccount

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
import com.hayakai.databinding.ActivityCreateAccountBinding
import com.hayakai.ui.login.LoginActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateAccountBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
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
        binding.btnRegister.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)

        binding.etName.addTextChangedListener {
            validateName()
        }

        binding.etEmail.addTextChangedListener {
            validateEmail()
        }

        binding.etPassword.addTextChangedListener {
            validatePassword()
        }

        binding.etConfirmPassword.addTextChangedListener {
            validateConfirmPassword()
        }
    }

    private fun validateName(): Boolean {
        return when {
            binding.etName.text.toString().isEmpty() -> {
                binding.tilName.error = getString(R.string.ed_name_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilName.error = null
                true
            }
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

    private fun validateConfirmPassword(): Boolean {
        return when {
            binding.etConfirmPassword.text.toString().isEmpty() -> {
                binding.tilConfirmPassword.error =
                    getString(R.string.ed_password_error_msg_is_empty)
                false
            }

            binding.etConfirmPassword.text.toString() != binding.etPassword.text.toString() -> {
                binding.tilConfirmPassword.error =
                    getString(R.string.ed_password_error_msg_not_match)
                false
            }

            else -> {
                binding.tilConfirmPassword.error = null
                true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_register -> {
                if (!validateName() or !validateEmail() or !validatePassword() or !validateConfirmPassword()) return

                val name = binding.etName.text.toString()
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                registerViewModel.register(name, email, password).observe(this) { result ->
                    when (result) {
                        is MyResult.Loading -> {
                            binding.btnRegister.isEnabled = false
                        }

                        is MyResult.Success -> {
                            binding.btnRegister.isEnabled = true
                            Toast.makeText(
                                this,
                                getString(R.string.register_success), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

                        }

                        is MyResult.Error -> {
                            binding.btnRegister.isEnabled = true
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            R.id.back_button -> {
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }
}