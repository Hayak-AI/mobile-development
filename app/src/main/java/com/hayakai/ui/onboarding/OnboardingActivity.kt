package com.hayakai.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hayakai.R
import com.hayakai.databinding.ActivityOnboardingBinding
import com.hayakai.navigation.BottomNavigation
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.login.LoginActivity
import com.hayakai.ui.register.RegisterActivity
import com.hayakai.utils.ViewModelFactory

class OnboardingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityOnboardingBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val oneTimePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Izin diterima", Toast.LENGTH_SHORT).show()
            } else {
                Snackbar.make(
                    binding.root,
                    "Gagal mendapatkan izin",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Coba lagi") {
                        val intent = Intent()
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = android.net.Uri.fromParts("package", packageName, null)
                        startActivity(intent)
                    }.show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    MaterialAlertDialogBuilder(this)
                        .setView(R.layout.allow_all_time)
                        .setTitle("Izinkan Aplikasi")
                        .setMessage("Hayak.AI membutuhkan izin untuk mengakses lokasi meskipun aplikasi telah ditutup untuk deteksi suara dan notifikasi Anda.")
                        .setPositiveButton("Izinkan") { dialog, _ ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                oneTimePermissionLauncher.launch(
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                )
                            }
                        }.setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                            Toast.makeText(this, "Tidak mendapatkan ijin", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .show()

                }

                else -> {
                    Snackbar.make(
                        binding.root,
                        "Gagal mendapatkan izin",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("Coba lagi") {
                            val intent = Intent()
                            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                            intent.data = android.net.Uri.fromParts("package", packageName, null)
                            startActivity(intent)
                        }.show()
                }
            }

        }

    private fun requestPermissions() {
        var isAllGranted = true
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                isAllGranted = false
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                isAllGranted = false
            }

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED -> {
                isAllGranted = false
            }

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED -> {
                isAllGranted = false
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                isAllGranted = false
            }
        }

        if (isAllGranted) {
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Izinkan Aplikasi")
            .setMessage("Hayak.AI membutuhkan izin yang selalu aktif meskipun aplikasi ditutup untuk mengakses lokasi, perekaman audio, sms, dan notifikasi Anda.")
            .setPositiveButton("Izinkan") { dialog, _ ->
                val perms = arrayListOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms.add(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
                requestPermissionLauncher.launch(
                    perms.toTypedArray()
                )
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Tidak mendapatkan ijin", Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        requestPermissions()
        setupAction()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        sessionViewModel.getSession().observe(this) {
            if (it.token.isNotEmpty()) {
                val intent = Intent(this@OnboardingActivity, BottomNavigation::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_register -> {
                val intent = Intent(this@OnboardingActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }


}