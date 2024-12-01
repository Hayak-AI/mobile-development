package com.hayakai.ui.newpost

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.hayakai.R
import com.hayakai.data.local.model.LocationModel
import com.hayakai.data.remote.dto.LocationNewPost
import com.hayakai.data.remote.dto.NewPostDto
import com.hayakai.databinding.ActivityNewPostBinding
import com.hayakai.ui.newmapreport.SelectMapActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class NewPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNewPostBinding
    private val newPostViewModel: NewPostViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var location: LocationModel? = null

    private val resultSelectMap =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == SelectMapActivity.RESULT_CODE && result.data != null) {

                location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        SelectMapActivity.EXTRA_PARCEL,
                        LocationModel::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra(SelectMapActivity.EXTRA_PARCEL)
                } ?: LocationModel()

                binding.tvAddLocation.text = location?.name
                binding.btnRemoveLocation.visibility = View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
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
        binding.etTitle.addTextChangedListener { validateTitle() }
        binding.etContent.addTextChangedListener { validateContent() }
        binding.clAddLocation.setOnClickListener(this)
        binding.btnRemoveLocation.setOnClickListener(this)
    }

    private fun validateTitle(): Boolean {
        return when {
            binding.etTitle.text.toString().isEmpty() -> {
                binding.tilTitle.error = getString(R.string.ed_title_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilTitle.error = null
                true
            }
        }
    }

    private fun validateContent(): Boolean {
        return when {
            binding.etContent.text.toString().isEmpty() -> {
                binding.tilContent.error = getString(R.string.ed_content_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilContent.error = null
                true
            }
        }
    }

    private fun validateCategory(): Boolean {
        return when {
            binding.actvCategory.text.toString().isEmpty() -> {
                binding.tilCategory.error = getString(R.string.ed_category_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilCategory.error = null
                true
            }
        }
    }

    private fun newPost(newPostDto: NewPostDto) {
        newPostViewModel.newPost(newPostDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Berhasil membuat postingan", Toast.LENGTH_SHORT).show()
                    finish()
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
            binding.backButton.id -> {
                finish()
            }

            R.id.cl_add_location -> {
                val intent = Intent(this, SelectMapActivity::class.java)
                resultSelectMap.launch(intent)
            }

            R.id.btn_remove_location -> {
                location = null
                binding.tvAddLocation.text = getString(R.string.hint_add_location)
                binding.btnRemoveLocation.visibility = View.GONE
            }

            binding.btnSave.id -> {
                if (!validateTitle() || !validateContent() || !validateCategory()) return

                val title = binding.etTitle.text.toString()
                val content = binding.etContent.text.toString()
                val category = binding.actvCategory.text.toString()

                newPost(
                    NewPostDto(
                        title = title,
                        content = content,
                        category = category,
                        if (location != null) LocationNewPost(
                            location!!.name,
                            location!!.latitude,
                            location!!.longitude
                        ) else null
                    )
                )
            }
        }
    }
}