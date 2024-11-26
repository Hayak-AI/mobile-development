package com.hayakai.ui.settingsemailpassword

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayakai.R
import com.hayakai.databinding.ActivitySettingsEmailPasswordBinding

class SettingsEmailPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingsEmailPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsEmailPasswordBinding.inflate(layoutInflater)
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
        binding.btnSave.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSave -> {
                // Save button clicked
            }

            binding.backButton -> {
                finish()
            }
        }
    }


}