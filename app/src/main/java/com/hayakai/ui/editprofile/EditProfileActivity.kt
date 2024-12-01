package com.hayakai.ui.editprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mycamera.reduceFileImage
import com.dicoding.picodiploma.mycamera.uriToFile
import com.hayakai.R
import com.hayakai.data.pref.UserModel
import com.hayakai.data.remote.dto.UpdateProfileDto
import com.hayakai.databinding.ActivityEditProfileBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val editProfileViewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var profile: UserModel? = null
    private var currentProfilePhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
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
        editProfileViewModel.getProfile().observe(this) { profile ->
            when (profile) {
                is MyResult.Loading -> {
//                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
//                    binding.progressIndicator.visibility = View.GONE
                    profile.data.let {
                        this.profile = it
                        binding.etFullName.setText(profile.data.name)
                        binding.etPhone.setText(profile.data.phone)
                        currentProfilePhoto = profile.data.image
                        Glide.with(this)
                            .load(profile.data.image)
                            .placeholder(R.drawable.mdi_user_outline)
                            .into(binding.userImage)
                    }
                }

                is MyResult.Error -> {
//                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.userImage.setOnClickListener(this)

        binding.etFullName.addTextChangedListener {
            validateName()
        }
        binding.etPhone.addTextChangedListener {
            validatePhoneNumber()
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

    private fun updateProfile(updateProfileDto: UpdateProfileDto) {
        editProfileViewModel.updateProfile(updateProfileDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show()
                }

                is MyResult.Error -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileImage: File = uriToFile(uri, this).reduceFileImage()
            val requestFile = fileImage.asRequestBody("image/*".toMediaTypeOrNull())
            val photo = MultipartBody.Part.createFormData("file", fileImage.name, requestFile)
            uploadProfilePhoto(photo)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun uploadProfilePhoto(photo: MultipartBody.Part) {
        editProfileViewModel.uploadProfilePhoto(photo).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnSave.isEnabled = true
                    currentProfilePhoto = result.data.imageUrl
                    Glide.with(this)
                        .load(result.data.imageUrl)
                        .placeholder(R.drawable.mdi_user_outline)
                        .into(binding.userImage)
                    Toast.makeText(
                        this,
                        getString(R.string.photo_profile_updated), Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is MyResult.Error -> {
                    binding.btnSave.isEnabled = true
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
                if (!validateName() or !validatePhoneNumber()) return

                val name = binding.etFullName.text.toString()
                val phoneNumber = binding.etPhone.text.toString()
                val profilePhoto = currentProfilePhoto.toString()

                updateProfile(UpdateProfileDto(name, phoneNumber, profilePhoto))
            }

            R.id.user_image -> {
                startGallery()
                Toast.makeText(this, "Upload User Image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}