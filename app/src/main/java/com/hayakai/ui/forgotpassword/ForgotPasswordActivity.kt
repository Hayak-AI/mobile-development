package com.hayakai.ui.forgotpassword

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
import com.hayakai.data.remote.dto.ForgotPasswordDto
import com.hayakai.data.remote.dto.ResetPasswordDto
import com.hayakai.databinding.ActivityForgotPasswordBinding
import com.hayakai.ui.login.LoginActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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
        binding.btnUpdatePassword.setOnClickListener(this)
        binding.btnSendOtp.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)

        binding.etEmail.addTextChangedListener {
            validateEmail()
        }

        binding.etOtp.addTextChangedListener {
            validateOtp()
        }

        binding.etPassword.addTextChangedListener {
            validatePassword()
        }

        binding.etConfirmPassword.addTextChangedListener {
            validateConfirmPassword()
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

    private fun validateOtp(): Boolean {
        return when {
            binding.etOtp.text.toString().isEmpty() -> {
                binding.tilOtp.error = getString(R.string.ed_otp_error_msg_is_empty)
                false
            }

            binding.etOtp.text.toString().length < 5 -> {
                binding.tilOtp.error = getString(R.string.ed_otp_error_msg_is_invalid)
                false
            }

            else -> {
                binding.tilOtp.error = null
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

    private fun forgotPassword(forgotPasswordDto: ForgotPasswordDto) {
        forgotPasswordViewModel.forgotPassword(forgotPasswordDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnUpdatePassword.isEnabled = false
                    binding.btnSendOtp.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnUpdatePassword.isEnabled = true
                    binding.btnSendOtp.isEnabled = true
                    Toast.makeText(this, "Token sent", Toast.LENGTH_SHORT).show()
                }

                is MyResult.Error -> {
                    binding.btnUpdatePassword.isEnabled = true
                    binding.btnSendOtp.isEnabled = true
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun resetPassword(resetPasswordDto: ResetPasswordDto) {
        forgotPasswordViewModel.resetPassword(resetPasswordDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnUpdatePassword.isEnabled = false
                    binding.btnSendOtp.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnUpdatePassword.isEnabled = true
                    binding.btnSendOtp.isEnabled = true
                    Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                is MyResult.Error -> {
                    binding.btnUpdatePassword.isEnabled = true
                    binding.btnSendOtp.isEnabled = true
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update_password -> {
                if (!validateOtp() or !validatePassword() or !validateConfirmPassword()) return

                val otp = binding.etOtp.text.toString()
                val password = binding.etPassword.text.toString()

                val resetPasswordDto = ResetPasswordDto(otp.toInt(), password)

                resetPassword(resetPasswordDto)
            }

            R.id.btn_send_otp -> {
                if (!validateEmail()) return

                val email = binding.etEmail.text.toString()

                val forgotPasswordDto = ForgotPasswordDto(email)

                forgotPassword(forgotPasswordDto)
            }

            R.id.back_button -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

}