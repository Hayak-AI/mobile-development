package com.hayakai.ui.home

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.Contact
import com.hayakai.data.pref.SettingsModel
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.databinding.FragmentHomeBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.detailcontact.DetailContactActivity
import com.hayakai.ui.home.MyService.Companion.TAG
import com.hayakai.ui.newcontact.NewContactActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var settingsModel: SettingsModel? = null

    private lateinit var contactList: List<Contact>

    private var fromClick = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private fun createLocationRequest() {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val interval = TimeUnit.SECONDS.toMillis(30)
        val maxWaitTime = TimeUnit.SECONDS.toMillis(1)
        locationRequest = LocationRequest.Builder(
            priority,
            interval
        ).apply {
            setMaxUpdateDelayMillis(maxWaitTime)
        }.build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            Log.d(TAG, "createLocationRequest: onSuccess")
        }.addOnFailureListener {
            Log.e(TAG, "createLocationRequest: onFailure")
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionViewModel.getSession().observe(viewLifecycleOwner) { session ->
            if (session.token.isEmpty()) {
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                setupViewModel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }


    private fun setupViewModel() {
        viewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            when (profile) {
                is MyResult.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.homeSayName.text =
                        getString(R.string.title_say_name, profile.data.name)
                }

                is MyResult.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.getSettings().observe(viewLifecycleOwner) { profile ->
            when (profile) {
                is MyResult.Loading -> {
                }

                is MyResult.Success -> {
                    settingsModel = profile.data
                    binding.voiceDetection.setImageResource(
                        if (profile.data.voiceDetection) R.drawable.voice_detection_on
                        else R.drawable.voice_detection_off
                    )

                    binding.titleVoiceDetection.text =
                        if (profile.data.voiceDetection) getString(R.string.title_voice_detection_on) else getString(
                            R.string.title_voice_detection_off
                        )

                    binding.mbtgVoiceSensitivity.check(
                        when (profile.data.voiceSensitivity) {
                            "low" -> R.id.btn_low
                            "medium" -> R.id.btn_medium
                            else -> R.id.btn_high
                        }
                    )
                    if (context?.isServiceRunning(MyService::class.java) == true && !profile.data.voiceDetection && fromClick) {
                        requireActivity().stopService(
                            Intent(
                                requireContext(),
                                MyService::class.java
                            )
                        )
                    }
                }

                is MyResult.Error -> {
                    Toast.makeText(requireContext(), profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getContacts().observe(viewLifecycleOwner) { contacts ->
            when (contacts) {
                is MyResult.Loading -> {
                }

                is MyResult.Success -> {
                    binding.tvNotFound.visibility =
                        if (contacts.data.isEmpty()) View.VISIBLE else View.GONE
                    val layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerView.layoutManager = layoutManager
                    val adapter = ContactListAdapter(
                        onClick = { contact ->
                            val intent = Intent(requireContext(), DetailContactActivity::class.java)
                            intent.putExtra(DetailContactActivity.EXTRA_CONTACT, contact)
                            startActivity(intent)
                        }
                    )
                    contactList = contacts.data
                    adapter.submitList(contacts.data)
                    binding.recyclerView.adapter = adapter

                    val audioClassificationService = Intent(requireContext(), MyService::class.java)
                    audioClassificationService.putParcelableArrayListExtra(
                        MyService.EXTRA_CONTACT,
                        contactList as ArrayList<Contact>
                    )
                    audioClassificationService.putExtra(
                        MyService.EXTRA_THRESHOLD,
                        when (settingsModel?.voiceSensitivity) {
                            "low" -> 0.8f
                            "medium" -> 0.5f
                            else -> 0.2f
                        }
                    )
                    try {

                        if (settingsModel?.voiceDetection!!) {
                            if (context?.isServiceRunning(MyService::class.java) == false) {
                                requireActivity().startForegroundService(
                                    audioClassificationService
                                )
                            }
                        } else {
                            requireActivity().stopService(audioClassificationService)
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", e.message.toString())
                    }
                }

                is MyResult.Error -> {
                    Toast.makeText(requireContext(), contacts.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Permission not granted")
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        val geocoder = Geocoder(requireContext(), resources.configuration.locales[0])
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            lifecycleScope.launch {
                try {
                    val addresses =
                        withContext(Dispatchers.IO) {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        }

                    viewModel.getAllNews(addresses?.firstOrNull()?.locality ?: "Disini")
                        .observe(viewLifecycleOwner) { news ->
                            when (news) {
                                is MyResult.Loading -> {
                                    binding.loadingSafetyScore.visibility = View.VISIBLE
                                    binding.tvSafetyScore.visibility = View.GONE
                                    binding.tvSafetyScore.visibility = View.GONE
                                }

                                is MyResult.Success -> {
                                    binding.loadingSafetyScore.visibility = View.GONE
                                    binding.tvSafetyScore.visibility = View.VISIBLE
                                    binding.tvSafetyScore.text =
                                        getString(
                                            R.string.title_score,
                                            news.data.firstOrNull()?.safetyScore ?: 0
                                        )
                                }

                                is MyResult.Error -> {
                                    binding.loadingSafetyScore.visibility = View.GONE
                                    binding.tvSafetyScore.visibility = View.VISIBLE
                                    Toast.makeText(requireContext(), news.error, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        createLocationRequest()
        createLocationCallback()


        return root
    }


    private fun setupAction() {
        binding.btnProfile.setOnClickListener(this)
        binding.btnAddContact.setOnClickListener(this)
        binding.voiceDetection.setOnClickListener(this)
        binding.mbtgVoiceSensitivity.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val updateUserPreferenceDto = UpdateUserPreferenceDto(
                voice_sensitivity = when (checkedId) {
                    R.id.btn_low -> "low"
                    R.id.btn_medium -> "medium"
                    else -> "high"
                }
            )
            updateSettings(updateUserPreferenceDto)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateSettings(updateUserPreferenceDto: UpdateUserPreferenceDto) {
        viewModel.updateSettings(updateUserPreferenceDto).observe(viewLifecycleOwner) { result ->
            when (result) {
                is MyResult.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    settingsModel = result.data
                }

                is MyResult.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun obtainViewModel(): HomeViewModel {
        val factory = ViewModelFactory.getInstance(requireActivity())
        return ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_profile -> {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_add_contact -> {
                val intent = Intent(requireContext(), NewContactActivity::class.java)
                startActivity(intent)
            }

            R.id.voice_detection -> {

                fromClick = true
                val updateUserPreferenceDto = UpdateUserPreferenceDto(
                    voice_detection = !settingsModel?.voiceDetection!!,
                )
                val audioClassificationService = Intent(requireContext(), MyService::class.java)
                audioClassificationService.putParcelableArrayListExtra(
                    MyService.EXTRA_CONTACT,
                    contactList as ArrayList<Contact>
                )
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(if (settingsModel?.voiceDetection!!) "Konfirmasi Menonaktifkan Deteksi Suara" else "Konfirmasi Pengaktifan Deteksi Suara")
                    .setMessage(if (settingsModel?.voiceDetection!!) "Deteksi suara teriakan untuk pengiriman pesan darurat akan dinonaktifkan. Apakah Anda yakin?" else "Aplikasi akan mendeteksi suara teriakan untuk memicu pengiriman pesan darurat kepada kontak yang terdaftar dan yang diberitahu. Apakah Anda yakin ingin mengaktifkan deteksi suara?")
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                        if (settingsModel?.voiceDetection!!) {
                            requireActivity().stopService(audioClassificationService)
                        } else {
                            requireActivity().startForegroundService(audioClassificationService)
                        }

                        updateSettings(updateUserPreferenceDto)
                        dialog.dismiss()
                    }
                    .show()


            }
        }
    }
}