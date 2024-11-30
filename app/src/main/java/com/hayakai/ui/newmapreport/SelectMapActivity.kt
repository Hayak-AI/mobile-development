package com.hayakai.ui.newmapreport

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.hayakai.R
import com.hayakai.data.local.model.LocationModel
import com.hayakai.databinding.ActivitySelectMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectMapActivity : AppCompatActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private lateinit var binding: ActivitySelectMapBinding
    private lateinit var mMap: GoogleMap

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)

        binding = ActivitySelectMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val geocoder = Geocoder(this@SelectMapActivity, resources.configuration.locales[0])

        mMap.setOnMapClickListener { latLng ->
            lifecycleScope.launch {
                @Suppress("DEPRECATION")
                try {
                    val addresses =
                        withContext(Dispatchers.IO) {
                            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        }

                    addresses?.forEach { address ->
                        mMap.clear()
                        mMap.addMarker(
                            MarkerOptions().position(latLng).title(address.getAddressLine(0))
                        )
                        Snackbar.make(binding.root, "Simpan lokasi?", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ya") {
                                val intent = Intent()
                                val parcel = LocationModel(
                                    address.getAddressLine(0),
                                    latLng.latitude,
                                    latLng.longitude,
                                )
                                intent.putExtra(EXTRA_PARCEL, parcel)
                                setResult(RESULT_CODE, intent)
                                finish()
                            }
                            .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@SelectMapActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }


            }
        }

        getMyLocation()
        setMapStyle()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
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
                        this,
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

    companion object {
        const val TAG = "SelectMapActivity"
        const val EXTRA_PARCEL = "extra_parcel"
        const val RESULT_CODE = 110
    }

}