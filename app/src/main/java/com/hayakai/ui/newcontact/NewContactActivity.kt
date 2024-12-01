package com.hayakai.ui.newcontact

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
import com.hayakai.data.remote.dto.NewContactDto
import com.hayakai.databinding.ActivityNewContactBinding
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class NewContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNewContactBinding

    private val newContactViewModel: NewContactViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewContactBinding.inflate(layoutInflater)
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
        binding.backButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        binding.etFullName.addTextChangedListener {
            validateName()
        }
        binding.etPhone.addTextChangedListener {
            validatePhoneNumber()
        }
        binding.etEmail.addTextChangedListener {
            validateEmail()
        }
        binding.etMessage.addTextChangedListener {
            validateMessage()
        }
    }

    private fun validateName(): Boolean {
        return when {
            binding.etFullName.text.toString().isEmpty() -> {
                binding.tilFullName.error = getString(R.string.ed_name_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilFullName.error = null
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

    private fun validatePhoneNumber(): Boolean {
        return when {
            binding.etPhone.text.toString().isEmpty() -> {
                binding.tilPhone.error = getString(R.string.ed_phone_error_msg_is_empty)
                false
            }

            !Patterns.PHONE.matcher(binding.etPhone.text.toString()).matches() -> {
                binding.tilPhone.error = getString(R.string.ed_phone_error_msg_is_invalid)
                false
            }

            else -> {
                binding.tilPhone.error = null
                true
            }
        }
    }

    private fun validateMessage(): Boolean {
        return when {
            binding.etMessage.text.toString().isEmpty() -> {
                binding.tilMessage.error = getString(R.string.ed_message_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilMessage.error = null
                true
            }
        }
    }

    private fun newContact(newContactDto: NewContactDto) {
        newContactViewModel.newContact(newContactDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {

                }

                is MyResult.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.contact_added_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                is MyResult.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.btn_save -> {
                if (!validateName() or !validatePhoneNumber() or !validateEmail() or !validateMessage()) return

                val name = binding.etFullName.text.toString()
                val phoneNumber = binding.etPhone.text.toString()
                val email = binding.etEmail.text.toString()
                val message = binding.etMessage.text.toString()

                newContact(
                    NewContactDto(
                        name,
                        email,
                        phoneNumber,
                        message
                    )
                )
            }
        }
    }
}