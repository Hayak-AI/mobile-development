package com.hayakai.ui.detailcontact

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.Contact
import com.hayakai.data.remote.dto.DeleteContactDto
import com.hayakai.data.remote.dto.UpdateContactDto
import com.hayakai.databinding.ActivityDetailContactBinding
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class DetailContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailContactBinding

    private val detailContactViewModel: DetailContactViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
        setupContactData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupContactData() {
        contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CONTACT, Contact::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CONTACT)
        } ?: Contact()

        binding.etFullName.setText(contact.name)
        binding.etPhone.setText(contact.phone)
        binding.etEmail.setText(contact.email)
        binding.etMessage.setText(contact.message)
        binding.switchNotify.isChecked = contact.notify
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)

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

    private fun deleteContact(dialog: DialogInterface, contactId: Int) {
        detailContactViewModel.deleteContact(DeleteContactDto(contactId)).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {

                }

                is MyResult.Success -> {
                    dialog.dismiss()
                    finish()
                    Toast.makeText(
                        this,
                        getString(R.string.success_delete_contact),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MyResult.Error -> {
                    dialog.dismiss()
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
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
            !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                .matches() && binding.etEmail.text.toString().isNotEmpty() -> {
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

    private fun updateContact(updateContactDto: UpdateContactDto) {
        detailContactViewModel.updateContact(updateContactDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {

                }

                is MyResult.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.success_update_contact),
                        Toast.LENGTH_SHORT
                    ).show()
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
                val notify = binding.switchNotify.isChecked

                updateContact(
                    UpdateContactDto(
                        contact.id,
                        name,
                        email,
                        phoneNumber,
                        notify,
                        message
                    )
                )

            }

            R.id.btn_delete -> {
                MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_DeleteConfirmation)
                    .setTitle(getString(R.string.title_delete_contact))
                    .setMessage(getString(R.string.content_delete_contact))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        deleteContact(dialog, contact.id)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
    }
}