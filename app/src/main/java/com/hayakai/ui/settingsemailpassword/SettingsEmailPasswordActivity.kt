package com.hayakai.ui.settingsemailpassword

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
import com.hayakai.data.remote.dto.UpdateEmailPassDto
import com.hayakai.databinding.ActivitySettingsEmailPasswordBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class SettingsEmailPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingsEmailPasswordBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val settingsEmailPasswordViewModel: SettingsEmailPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsEmailPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        sessionViewModel.getSession().observe(this) {
            if (it.token.isEmpty()) {
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                setupViewModel()
            }
        }

        setupAction()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        settingsEmailPasswordViewModel.getProfile().observe(this) { profile ->
            when (profile) {
                is MyResult.Loading -> {
                    // Show loading
                }

                is MyResult.Success -> {
                    profile.data.let {
                        binding.etEmail.setText(it.email)
                    }
                }

                is MyResult.Error -> {
                    Toast.makeText(this, profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)
        binding.etEmail.addTextChangedListener {
            validateEmail()
        }


        binding.etOldPassword.addTextChangedListener {
            validateOldPassword()
        }

        binding.etNewPassword.addTextChangedListener {
            validateNewPassword()
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

    private fun validateOldPassword(): Boolean {
        return when {
            binding.etOldPassword.text.toString().isEmpty() -> {
                binding.tilOldPassword.error = getString(R.string.ed_password_error_msg_is_empty)
                false
            }

            binding.etOldPassword.text.toString().length < 8 -> {
                binding.tilOldPassword.error =
                    getString(R.string.ed_password_error_msg_invalid_length)
                false
            }

            else -> {
                binding.tilOldPassword.error = null
                true
            }
        }
    }

    private fun validateNewPassword(): Boolean {
        return when {
            binding.etNewPassword.text.toString().isEmpty() -> {
                binding.tilNewPassword.error =
                    getString(R.string.ed_password_error_msg_is_empty)
                false
            }

            binding.etNewPassword.text.toString().length < 8 -> {
                binding.tilNewPassword.error =
                    getString(R.string.ed_password_error_msg_invalid_length)
                false
            }

            else -> {
                binding.tilNewPassword.error = null
                true
            }
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.btnSave -> {
                val email = binding.etEmail.text.toString()
                val oldPassword = binding.etOldPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()

                if (email != "" && oldPassword != "" && newPassword != "") {
                    if (!validateEmail() or !validateOldPassword() or !validateNewPassword()) return
                    val updateEmailPassDto = UpdateEmailPassDto(email, oldPassword, newPassword)
                    settingsEmailPasswordViewModel.updateEmailPassword(updateEmailPassDto)
                        .observe(this) { result ->
                            when (result) {
                                is MyResult.Loading -> {
                                    // Show loading
                                }

                                is MyResult.Success -> {
                                    Toast.makeText(
                                        this,
                                        "Email dan password berhasil diperbarui",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is MyResult.Error -> {
                                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                } else if (email != "") {
                    if (!validateEmail()) return
                    val updateEmailPassDto = UpdateEmailPassDto(email)
                    settingsEmailPasswordViewModel.updateEmailPassword(updateEmailPassDto)
                        .observe(this) { result ->
                            when (result) {
                                is MyResult.Loading -> {
                                    // Show loading
                                }

                                is MyResult.Success -> {
                                    Toast.makeText(
                                        this,
                                        "Email berhasil diperbarui",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is MyResult.Error -> {
                                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }

            }

            binding.backButton -> {
                finish()
            }
        }
    }


}