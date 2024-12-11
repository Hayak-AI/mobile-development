package com.hayakai.ui.home

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.hayakai.R
import com.hayakai.data.local.entity.Contact
import com.hayakai.data.remote.dto.AddUserToEmergencyDto
import com.hayakai.data.remote.dto.LocationEmergency
import com.hayakai.di.Injection
import com.hayakai.navigation.BottomNavigation
import com.hayakai.utils.AudioClassifierHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.label.Category
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it -> it.service.className == service.name }
}

class MyService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, BottomNavigation::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Hayak AI")
            .setContentText("Saat ini sedang berjalan")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = CHANNEL_NAME
        notificationBuilder.setChannelId(CHANNEL_ID)
        mNotificationManager.createNotificationChannel(channel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        }
        return notificationBuilder.build()
    }

    private fun createNotification(contact: Contact): Notification {
        val notificationIntent = Intent(this, BottomNavigation::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Hayak AI - Darurat")
            .setContentText("Pesan darurat telah dikirim ke ${contact.name}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = CHANNEL_NAME
        notificationBuilder.setChannelId(CHANNEL_ID)
        mNotificationManager.createNotificationChannel(channel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        }
        return notificationBuilder.build()

    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sentPI: PendingIntent =
            PendingIntent.getBroadcast(
                this, 0, Intent("SMS_SENT"),
                PendingIntent.FLAG_IMMUTABLE
            )
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, sentPI, null)
    }

    private lateinit var audioClassifierHelper: AudioClassifierHelper
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    suspend fun sendEmergency(addUserToEmergencyDto: AddUserToEmergencyDto) {
        try {
            Injection.provideEmergencyRepository(this@MyService)
                .addUserToEmergency(addUserToEmergencyDto)
                .collect {
                    Log.d(TAG, "onStartCommand: $it")
                }
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand: ${e.message}")
        }
    }

    private fun initializeAudioClassifierHelper(
        contactList: List<Contact>,
        threshold: Float = 0.2f
    ) {

        audioClassifierHelper = AudioClassifierHelper(
            threshold = threshold,
            context = this,
            classifierListener = object : AudioClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Log.e(TAG, error)
                }

                override fun onResults(results: List<Category>, inferenceTime: Long) {
                    if (PermissionChecker.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PermissionChecker.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PermissionChecker.PERMISSION_GRANTED
                    ) {
                        Log.e(TAG, "Permission not granted")
                    }
                    serviceScope.launch {
                        results.let { it ->
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                Log.d(TAG, "onResults: $it, $location")
                            }
                            if (it.isNotEmpty() && it[0].score > 0.9 && it[0].index == 1) {
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    serviceScope.launch {
                                        sendEmergency(
                                            AddUserToEmergencyDto(
                                                description = "Pengguna dalam bahaya",
                                                location = LocationEmergency(
                                                    name = "Lokasi pengguna",
                                                    latitude = location.latitude,
                                                    longitude = location.longitude
                                                )
                                            )
                                        )
                                    }
                                }
                                val notificationManager =
                                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                                contactList.forEach { contact ->
                                    if (contact.notify) {
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                            val message =
                                                "Halo, ${contact.name}\nPesan saya adalah ${contact.message}\nSaya dalam bahaya di sini: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                                            sendSMS(contact.phone, message)
                                            println(
                                                "Mengirim pesan ke ${contact.name} (${contact.phone})"
                                            )
                                            createNotification(contact).let {
                                                startForeground(NOTIFICATION_ID, it)
                                            }
                                        }.addOnFailureListener {
                                            val message =
                                                "Halo, ${contact.name}\nPesan saya adalah ${contact.message}\nSaya dalam bahaya dan saya tidak bisa mendapatkan lokasi saya"
                                            sendSMS(contact.phone, message)
                                            println(
                                                "Mengirim pesan ke ${contact.name} (${contact.phone})"
                                            )
                                        }
                                    } else {
                                        val message =
                                            "Halo, ${contact.name}\nPesan saya adalah ${contact.message}\nSaya dalam bahaya dan saya tidak bisa mendapatkan lokasi saya"
                                        sendSMS(contact.phone, message)
                                        println(
                                            "Mengirim pesan ke ${contact.name} (${contact.phone})"
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun createLocationRequest() {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val interval = TimeUnit.SECONDS.toMillis(1)
        val maxWaitTime = TimeUnit.SECONDS.toMillis(1)
        locationRequest = LocationRequest.Builder(
            priority,
            interval
        ).apply {
            setMaxUpdateDelayMillis(maxWaitTime)
        }.build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
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
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
                }
            }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }


        Log.d(TAG, "Service dijalankan...")
        val contactList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableArrayListExtra(EXTRA_CONTACT, Contact::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableArrayListExtra(EXTRA_CONTACT)
        } ?: ArrayList<Contact>()

        val threshold = intent?.getFloatExtra(EXTRA_THRESHOLD, 0.2f) ?: 0.2f


        serviceScope.launch {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MyService)
            createLocationRequest()
            createLocationCallback()
            initializeAudioClassifierHelper(contactList, threshold)
            audioClassifierHelper.startAudioClassification()

        }

        return START_STICKY


    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            audioClassifierHelper.stopAudioClassification()
            serviceJob.cancel()
            Log.d(TAG, "onDestroy: Service dihentikan")
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        internal val TAG = MyService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "hayak_01"
        private const val CHANNEL_NAME = "hayak channel"
        const val EXTRA_THRESHOLD = "extra_threshold"
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

}