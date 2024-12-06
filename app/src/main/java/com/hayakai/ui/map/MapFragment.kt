package com.hayakai.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapColorScheme
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hayakai.R
import com.hayakai.data.local.entity.MapReport
import com.hayakai.databinding.FragmentMapBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.home.MyService
import com.hayakai.ui.mapreportpost.MapReportPostFragment
import com.hayakai.ui.newmapreport.NewMapReportActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import com.hayakai.utils.isDarkMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    OnMapsSdkInitializedCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val boundsBuilder = LatLngBounds.Builder()

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val viewModel: MapViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private var btnToggleMyReport = false
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
            Log.d(MyService.TAG, "createLocationRequest: onSuccess")
        }.addOnFailureListener {
            Log.e(MyService.TAG, "createLocationRequest: onFailure")
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(
                        MyService.TAG,
                        "onLocationResult: " + location.latitude + ", " + location.longitude
                    )
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(savedInstanceState)
        if (savedInstanceState != null) {
            btnToggleMyReport = savedInstanceState.getBoolean("btnToggleMyReport")
            if (btnToggleMyReport) {
                binding.btnListReport.text = "Semua laporan"
            } else {
                binding.btnListReport.text = "Laporan saya"
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        createLocationRequest()
        createLocationCallback()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("btnToggleMyReport", btnToggleMyReport)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d(
                "MapsDemo",
                "The latest version of the renderer is used."
            )

            MapsInitializer.Renderer.LEGACY -> Log.d(
                "MapsDemo",
                "The legacy version of the renderer is used."
            )
        }
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }


    internal inner class CustomInfoWindowAdapter : InfoWindowAdapter {
        private val window: View =
            layoutInflater.inflate(R.layout.item_report_map, null, false)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View {
            render(marker, window)
            return window
        }

        private fun render(marker: Marker, view: View) {
            val tag = marker.tag as MapReport
            val title = marker.title
            val tvTitle = view.findViewById<TextView>(R.id.name)
            if (!title.isNullOrBlank()) {
                tvTitle.text = title
            }

            val snippet = marker.snippet
            val tvDescription = view.findViewById<TextView>(R.id.description)
            if (!snippet.isNullOrBlank()) {
                tvDescription.text = snippet
            }

            val evindeceUrl = tag.evidenceUrl
            val evidence = view.findViewById<ImageView>(R.id.report_map_item_image)
            if (!evindeceUrl.isNullOrBlank()) {
                val request = ImageRequest.Builder(view.context)
                    .allowHardware(false)
                    .data(evindeceUrl)
                    .target(
                        onStart = { placeholder ->
                            // Handle the placeholder drawable.
                        },
                        onSuccess = { result ->

                            evidence.setImageBitmap(result.toBitmap())
                            if (marker.isInfoWindowShown) {
                                marker.hideInfoWindow()
                                marker.showInfoWindow()
                            }
                        },
                        onError = { error ->
                            // Handle the error drawable.
                        }
                    )
                    .build()
                view.context.imageLoader.enqueue(request)


            }

            val authorName = tag.userName
            val tvAuthorName = view.findViewById<TextView>(R.id.author_name)
            if (authorName.isNotBlank()) {
                tvAuthorName.text = authorName
            }
            val authorImage = tag.userImage
            val ivAuthorImage = view.findViewById<ImageView>(R.id.author_image)
            if (authorImage != null) {
                if (authorImage.isNotBlank()) {
                    val request = ImageRequest.Builder(view.context)
                        .allowHardware(false)
                        .data(authorImage)
                        .target(
                            onStart = { placeholder ->
                                // Handle the placeholder drawable.
                            },
                            onSuccess = { result ->
                                ivAuthorImage.setImageBitmap(result.toBitmap())
                                if (marker.isInfoWindowShown) {
                                    marker.hideInfoWindow()
                                    marker.showInfoWindow()
                                }
                            },
                            onError = { error ->
                                // Handle the error drawable.
                            }
                        )
                        .build()
                    view.context.imageLoader.enqueue(request)
                }
            } else {
                ivAuthorImage.setImageResource(R.drawable.fallback_user)
            }


            val verified = tag.verified
            val tvVerified = view.findViewById<TextView>(R.id.verified)
            if (verified) {
                tvVerified.text = "Verified"
            } else {
                tvVerified.text = "Not Verified"
            }


        }
    }

    private fun setupViewModel() {
        viewModel.getMapReports().observe(viewLifecycleOwner) { result ->
            when (result) {
                is MyResult.Loading -> {
                    Log.d(TAG, "Loading")
                }

                is MyResult.Success -> {
                    mMap.clear()
                    result.data.forEach { mapReport ->
                        print(mapReport)
                        if (btnToggleMyReport && !mapReport.byMe) {
                            return@forEach
                        }
                        val latLng = LatLng(mapReport.latitude, mapReport.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(mapReport.name)
                                .snippet(mapReport.description).infoWindowAnchor(
                                    0.5f,
                                    -0.1f
                                )
                        )?.tag = mapReport
                        boundsBuilder.include(latLng)
                    }
                    if (result.data.isNotEmpty()) {
                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )
                    }
                }

                is MyResult.Error -> {
                    println(result.error)
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
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
            Log.e(MyService.TAG, "Permission not granted")
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
                    viewModel.getAllNews(addresses?.firstOrNull()?.locality ?: "Disini")
                        .observe(viewLifecycleOwner) { news ->
                            when (news) {
                                is MyResult.Loading -> {
                                }

                                is MyResult.Success -> {

                                    binding.tvSafetyScore.text =
                                        getString(
                                            R.string.title_score,
                                            news.data.firstOrNull()?.safetyScore ?: 0
                                        )
                                    val adapter = NewsListAdapter()
                                    adapter.submitList(news.data)
                                    binding.recyclerView.adapter = adapter
                                }

                                is MyResult.Error -> {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapsInitializer.initialize(
            requireContext().applicationContext,
            MapsInitializer.Renderer.LATEST,
            this
        )


        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupAction()

        return root
    }

    private fun setupAction() {
        binding.btnReport.setOnClickListener(this)
        binding.btnListReport.setOnClickListener(this)
        binding.btnProfile.setOnClickListener(this)
        binding.btnNews.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.mapColorScheme = if (isDarkMode()) MapColorScheme.DARK else MapColorScheme.LIGHT
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()

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

        mMap.setOnInfoWindowClickListener {
            val mapReportPostFragment = MapReportPostFragment()
            mapReportPostFragment.arguments = Bundle().apply {
                putString("name", it.title)
                putString("description", it.snippet.toString())
                putString("latitude", it.position.latitude.toString())
                putString("longitude", it.position.longitude.toString())
                putParcelable(MapReportPostFragment.TAG, it.tag as MapReport)
            }
            mapReportPostFragment.show(childFragmentManager, MapReportPostFragment.TAG)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                context?.applicationContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MapsFragment"
    }

    var newsToggle = false

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_report -> {
                val intent = Intent(context, NewMapReportActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_list_report -> {
                if (btnToggleMyReport) {
                    binding.btnListReport.text = "Laporan saya"
                } else {
                    binding.btnListReport.text = "Semua laporan"
                }
                setupViewModel()

                btnToggleMyReport = !btnToggleMyReport
            }

            R.id.btn_profile -> {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_news -> {
                if (newsToggle) {
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                }
                newsToggle = !newsToggle
            }
        }
    }
}