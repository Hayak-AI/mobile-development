package com.hayakai.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hayakai.R
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
                    binding.voiceDetection.setImageResource(
                        if (profile.data.voiceDetection) R.drawable.voice_detection_on
                        else R.drawable.voice_detection_off
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()


        return root
    }

    private fun setupAction() {
        binding.btnProfile.setOnClickListener(this)
        binding.btnAddContact.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        }
    }
}