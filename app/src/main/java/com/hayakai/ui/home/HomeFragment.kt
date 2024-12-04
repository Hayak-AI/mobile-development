package com.hayakai.ui.home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hayakai.R
import com.hayakai.data.local.entity.Contact
import com.hayakai.data.pref.SettingsModel
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.databinding.FragmentHomeBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.detailcontact.DetailContactActivity
import com.hayakai.ui.newcontact.NewContactActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

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
                    try {

                        if (settingsModel?.voiceDetection!!) {
                            if (context?.isServiceRunning(MyService::class.java) == false) {
                                if (Build.VERSION.SDK_INT >= 26) {
                                    requireActivity().startForegroundService(
                                        audioClassificationService
                                    )
                                } else {
                                    requireActivity().startService(audioClassificationService)
                                }
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
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Permission is granted
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Permission is granted
                }


                else -> {
                    // Permission is denied
                }
            }
        }


    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        } else {
//            val intent = Intent()
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
//            intent.setData(uri)
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == 0) {
//                    println(result.data)
//                }
//            }.launch(intent)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
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
        requestPermission()


        return root
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
                fromClick = true
                val updateUserPreferenceDto = UpdateUserPreferenceDto(
                    settingsModel?.darkMode ?: false,
                    !settingsModel?.voiceDetection!!,
                    settingsModel?.locationTracking ?: false
                )
                val audioClassificationService = Intent(requireContext(), MyService::class.java)
                audioClassificationService.putParcelableArrayListExtra(
                    MyService.EXTRA_CONTACT,
                    contactList as ArrayList<Contact>
                )
                if (settingsModel?.voiceDetection!!) {
                    requireActivity().stopService(audioClassificationService)
                } else {


                    if (Build.VERSION.SDK_INT >= 26) {
                        requireActivity().startForegroundService(audioClassificationService)
                    } else {
                        requireActivity().startService(audioClassificationService)
                    }
                }

                updateSettings(updateUserPreferenceDto)
            }
        }
    }
}