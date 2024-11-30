package com.hayakai.ui.newmapreport

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import coil3.load
import com.dicoding.picodiploma.mycamera.reduceFileImage
import com.dicoding.picodiploma.mycamera.uriToFile
import com.hayakai.R
import com.hayakai.data.local.model.LocationModel
import com.hayakai.data.remote.dto.LocationData
import com.hayakai.data.remote.dto.NewReportMapDto
import com.hayakai.databinding.ActivityNewMapReportBinding
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class NewMapReportActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNewMapReportBinding
    private val newMapReportViewModel: NewMapReportViewModel by viewModels {
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
            }
        }

    private var currentEvidenceUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMapReportBinding.inflate(layoutInflater)
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

        binding.btnUploadEvidence.setOnClickListener(this)
        binding.etContent.addTextChangedListener { validateMessage() }
        binding.clAddLocation.setOnClickListener(this)

        binding.btnSave.setOnClickListener(this)
    }


    private fun validateMessage(): Boolean {
        return when {
            binding.etContent.text.toString().isEmpty() -> {
                binding.tilContent.error = getString(R.string.ed_message_error_msg_is_empty)
                false
            }

            else -> {
                binding.tilContent.error = null
                true
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
            uploadEvidence(photo)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun uploadEvidence(file: MultipartBody.Part) {
        newMapReportViewModel.uploadEvidence(file).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.ivEvidencePreview.visibility = View.GONE
                }

                is MyResult.Success -> {
                    binding.ivEvidencePreview.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = true
                    currentEvidenceUrl = result.data.imageUrl
                    binding.ivEvidencePreview.load(result.data.imageUrl)
                    Toast.makeText(
                        this,
                        getString(R.string.evidence_uploaded), Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is MyResult.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.ivEvidencePreview.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun newMapReport(newReportMapDto: NewReportMapDto) {
        newMapReportViewModel.newMapReport(newReportMapDto).observe(this) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.btnSave.isEnabled = false
                }

                is MyResult.Success -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Berhasil melaporkan lokasi", Toast.LENGTH_SHORT).show()
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
            R.id.back_button -> {
                finish()
            }

            R.id.btn_save -> {
                if (currentEvidenceUrl == null) {
                    Toast.makeText(this, "Please upload evidence", Toast.LENGTH_SHORT).show()
                    return
                }
                if (location == null) {
                    Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!validateMessage()) return

                val description = binding.etContent.text.toString()
                val evidence = currentEvidenceUrl ?: ""

                newMapReport(
                    NewReportMapDto(
                        description,
                        evidence,
                        LocationData(location?.name!!, location?.latitude!!, location?.longitude!!)
                    )
                )
            }

            R.id.btn_upload_evidence -> {
                startGallery()
            }

            R.id.cl_add_location -> {
                val intent = Intent(this, SelectMapActivity::class.java)
                resultSelectMap.launch(intent)
            }
        }
    }
}