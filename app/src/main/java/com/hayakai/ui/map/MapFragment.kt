package com.hayakai.ui.map

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hayakai.R
import com.hayakai.data.local.entity.MapReport
import com.hayakai.databinding.FragmentMapBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.mapreportpost.MapReportPostFragment
import com.hayakai.ui.newmapreport.NewMapReportActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory


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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()

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
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MapsFragment"
    }

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
        }
    }
}