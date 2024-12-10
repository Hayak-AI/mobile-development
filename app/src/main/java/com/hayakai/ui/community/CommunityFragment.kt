package com.hayakai.ui.community

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
import androidx.annotation.StringRes
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayoutMediator
import com.hayakai.R
import com.hayakai.databinding.FragmentCommunityBinding
import com.hayakai.ui.home.MyService.Companion.TAG
import com.hayakai.ui.newpost.NewPostActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CommunityFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCommunityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val communityViewModel: CommunityViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

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
                for (location in locationResult.locations) {
                    Log.d(
                        TAG,
                        "onLocationResult: " + location.latitude + ", " + location.longitude
                    )
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs = binding.tabLayout
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        createLocationRequest()
        createLocationCallback()

        setupViewModel()


        return root
    }

    private fun setupAction() {
        binding.floatingActionButton.setOnClickListener(this)
        binding.btnProfile.setOnClickListener(this)
    }

    private fun setupViewModel() {
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
                    println(addresses)
                    communityViewModel.getAllNews(addresses?.firstOrNull()?.locality ?: "Disini")
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
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.floatingActionButton.id -> {
                val intent = Intent(requireContext(), NewPostActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_profile -> {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.community_tab_text_1,
            R.string.community_tab_text_2
        )
    }
}