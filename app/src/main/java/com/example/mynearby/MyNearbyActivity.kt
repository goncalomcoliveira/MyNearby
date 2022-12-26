package com.example.mynearby

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mynearby.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import kotlin.properties.Delegates

class MyNearbyActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    private var _requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)   //parse and convert XML layout into layout bindings
        //setContentView(R.layout.activity_main)                //change Activity look to the design in activity_main.xml
        setContentView(binding.root)                            //change Activity look to the root design of our binding

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermissionsForLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    /**
     * Checks if location permissions are already granted, requesting them if not (see
     * [onRequestPermissionsResult]) and proceeding to [getLastLocation] if they are
     * to retrieve the last known location.
     */
    private fun checkPermissionsForLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                _requestCode
            )
        }
        else {
            // Get Location
        }
    }

    /**
     * Overrides the callback for the result from requesting permissions, notifying
     * the user when the permissions haven't yet been granted and proceeding to
     * [getLastLocation] to retrieve the last known location if the requested
     * permissions have been granted.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == _requestCode && grantResults.isNotEmpty()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get Location
            }
            else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)

                        LocationServices.getFusedLocationProviderClient(applicationContext)
                            .removeLocationUpdates(this)
                        if (result.locations.isNotEmpty()) {
                            val latestLocationIndex = result.locations.size - 1         //used index approach instead of lasLocation to avoid safe assertion
                            latitude = result.locations[latestLocationIndex].latitude
                            longitude = result.locations[latestLocationIndex].longitude
                            println(latitude)
                            println(longitude)
                            // open map when ready
                        }
                    }
                },
                Looper.getMainLooper()
            )
    }
}