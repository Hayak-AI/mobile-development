package com.hayakai.ui.home

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hayakai.R
import com.hayakai.data.pref.SettingsModel
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.databinding.FragmentHomeBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.detailcontact.DetailContactActivity
import com.hayakai.ui.newcontact.NewContactActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.AudioClassifierHelper
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import org.tensorflow.lite.support.label.Category

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var audioClassifierHelper: AudioClassifierHelper

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var settingsModel: SettingsModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionViewModel.getSession().observe(viewLifecycleOwner) { session ->
            if (session.token.isEmpty()) {
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {

                initializeAudioClassifierHelper()
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


                    if (profile.data.voiceDetection) {
                        requestPermissionsIfNeeded()
                        audioClassifierHelper.startAudioClassification()
                    } else {
                        audioClassifierHelper.stopAudioClassification()
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
                    adapter.submitList(contacts.data)
                    binding.recyclerView.adapter = adapter
                }

                is MyResult.Error -> {
                    Toast.makeText(requireContext(), contacts.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sentPI: PendingIntent =
            PendingIntent.getBroadcast(
                requireContext(), 0, Intent("SMS_SENT"),
                PendingIntent.FLAG_IMMUTABLE
            )
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, sentPI, null)
    }

    //    get permission to send sms
    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()
        requestPermission()
        sendSMS("+2126000000", "Some text here")


        return root
    }

    private fun initializeAudioClassifierHelper() {
        audioClassifierHelper = AudioClassifierHelper(
            context = requireContext(),
            classifierListener = object : AudioClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }

                override fun onResults(results: List<Category>, inferenceTime: Long) {
                    requireActivity().runOnUiThread {
                        results.let { it ->
                            if (it.isNotEmpty()) {

                                println(results)
                            } else {
                                println("No result")
                            }
                        }
                    }
                }
            }
        )
    }


    private fun setupAction() {
        binding.btnProfile.setOnClickListener(this)
        binding.btnAddContact.setOnClickListener(this)
        binding.voiceDetection.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                    Toast.makeText(requireContext(), "Settings updated", Toast.LENGTH_SHORT).show()
                }

                is MyResult.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                val updateUserPreferenceDto = UpdateUserPreferenceDto(
                    settingsModel?.darkMode ?: false,
                    !settingsModel?.voiceDetection!!,
                    settingsModel?.locationTracking ?: false
                )
                if (settingsModel?.voiceDetection!!) {
                    audioClassifierHelper.stopAudioClassification()
                } else {
                    requestPermissionsIfNeeded()
                    audioClassifierHelper.startAudioClassification()
                }
                updateSettings(updateUserPreferenceDto)
            }
        }
    }
}