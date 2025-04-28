package com.example.fatch_location

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import android.Manifest
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat

class MainActivity : FlutterActivity() {

    private val CHANNEL_NAME = "FATCH_LOACTION"
    private val LOCATION_PERMISSION_CODE = 1001
    private var methodResult: MethodChannel.Result? = null


    override fun configureFlutterEngine(flutterEngine: io.flutter.embedding.engine.FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME).setMethodCallHandler { call, result ->
            when (call.method) {
                "startlocationService" -> {
                    if (isLocationPermissionGranted()) {
                        launchLocationService()
                        result.success("Service Started Successfully")
                    } else {
                        methodResult = result
                        requestLocationPermissions()
                    }
                }

                "getlastLocation" -> {
                    val dbHelper = LocationDatabaseHelper(this)
                    val lastKnownLocation = dbHelper.getLastLocation()

                    if (lastKnownLocation != null) {
                        result.success(lastKnownLocation)
                    } else {
                        result.error("NO_DATA", "No location data available", null)
                    }
                }

                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val foreground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return fine && coarse && foreground
    }

    private fun requestLocationPermissions() {
        val permissionsNeeded = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), LOCATION_PERMISSION_CODE)
    }

    private fun launchLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService(serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE) {
            val allGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                launchLocationService()
                methodResult?.success("Service Started After Permission")
            } else {
                methodResult?.error("PERMISSION_DENIED", "User denied location permission", null)
            }
            methodResult = null
        }
    }
}