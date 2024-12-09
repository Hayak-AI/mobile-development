package com.hayakai.ui.editpost

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.local.model.LocationModel
import com.hayakai.data.remote.dto.LocationUpdatePost
import com.hayakai.data.remote.dto.UpdatePostDto
import com.hayakai.databinding.ActivityEditPostBinding
import com.hayakai.ui.newmapreport.SelectMapActivity
import com.hayakai.ui.newmapreport.SelectMapActivity.Companion.EXTRA_PARCEL
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class EditPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditPostBinding
    private val editPostViewModel: EditPostViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var location: LocationModel? = null

    private lateinit var post: CommunityPost

    private val resultSelectMap =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == SelectMapActivity.RESULT_CODE && result.data != null) {

                location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        EXTRA_PARCEL,
                        LocationModel::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra(EXTRA_PARCEL)
                } ?: LocationModel()

                binding.tvAddLocation.text = location?.name
                binding.btnRemoveLocation.visibility = View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val params =
            binding.tilContent.findViewById<ImageView>(com.google.android.material.R.id.text_input_end_icon)
                .layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM

        binding.tilContent
            .findViewById<ImageView>(com.google.android.material.R.id.text_input_end_icon)
            .setLayoutParams(params)
        supportActionBar?.hide()

        setupAction()
        setupData()

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
        binding.tilContent.setEndIconOnClickListener {
            editPostViewModel.generate(binding.etContent.text.toString() + " [Tanpa ada markdown ataupun html, cukup teks saja, kurang dari 800 huruf]")
                .observe(this) { result ->
                    when (result) {
                        is MyResult.Loading -> {
                            binding.btnSave.isEnabled = false
                        }

                        is MyResult.Success -> {
                            binding.btnSave.isEnabled = true
                            binding.etContent.setText(result.data)
                        }

                        is MyResult.Error -> {
                            binding.btnSave.isEnabled = true
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun setupData() {
        post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_POST, CommunityPost::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_POST)
        } ?: CommunityPost()

        binding.apply {
            etTitle.setText(post.title)
            etContent.setText(post.content)
            actvCategory.setText(post.category)
            if (post.locationName.isNotEmpty()) {
                location = LocationModel(
                    post.locationName,
                    post.latitude,
                    post.longitude
                )
                tvAddLocation.text = post.locationName
                btnRemoveLocation.visibility = View.VISIBLE
            }
        }
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

    private fun editPost(dialog: DialogInterface, updatePostDto: UpdatePostDto) {
        editPostViewModel.editPost(updatePostDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Berhasil memperbarui postingan", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                    val intent = Intent()
                    val parcel = CommunityPost(
                        updatePostDto.post_id,
                        updatePostDto.title,
                        updatePostDto.content,
                        updatePostDto.category,
                        post.userId,
                        post.userName,
                        post.userImage,
                        post.byMe,
                        post.createdAt,
                        post.updatedAt,
                        location?.name ?: "",
                        location?.latitude ?: 0.0,
                        location?.longitude ?: 0.0,

                        )
                    intent.putExtra(EXTRA_POST, parcel)
                    setResult(RESULT_CODE, intent)
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
                MaterialAlertDialogBuilder(
                    this,
                    R.style.MaterialAlertDialog_DeleteConfirmation
                )
                    .setTitle("Perbarui Postingan")
                    .setMessage("Apakah Anda yakin ingin memperbarui postingan ini?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        editPost(
                            dialog,
                            UpdatePostDto(
                                post.id,
                                title = title,
                                content = content,
                                category = category,
                                if (location != null) LocationUpdatePost(
                                    location!!.name,
                                    location!!.latitude,
                                    location!!.longitude
                                ) else null
                            )
                        )
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            }
        }
    }

    companion object {
        const val EXTRA_POST = "extra_post"
        const val RESULT_CODE = 200
    }
}