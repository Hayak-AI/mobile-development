package com.hayakai.ui.detailcontact

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayakai.R
import com.hayakai.databinding.ActivityDetailContactBinding

class DetailContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailContactBinding.inflate(layoutInflater)
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.btn_save -> {
                
            }
        }
    }


}