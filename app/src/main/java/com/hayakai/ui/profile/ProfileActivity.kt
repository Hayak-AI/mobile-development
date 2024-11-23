package com.hayakai.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayakai.R
import com.hayakai.databinding.ActivityProfileBinding
import com.hayakai.ui.editprofile.EditProfileActivity
import com.hayakai.ui.settingsemailpassword.SettingsEmailPasswordActivity

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
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
        binding.settingsPersonalInformation.setOnClickListener(this)
        binding.settingsEmailPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.settings_personal_information -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.settings_email_password -> {
                val intent = Intent(this, SettingsEmailPasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }
}