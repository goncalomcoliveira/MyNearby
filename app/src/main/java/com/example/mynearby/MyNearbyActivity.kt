package com.example.mynearby

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mynearby.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import kotlin.properties.Delegates

class MyNearbyActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentMarker: Marker

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    private var _requestCode = 1

    //popup fields
    private lateinit var closeButton: ImageButton

    private lateinit var goToLocation: ImageButton

    private lateinit var placeName: EditText
    private lateinit var placeDescription: EditText
    private lateinit var errorText: TextView
    private lateinit var createButton: Button
    private lateinit var closeButtonMarker: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)   //parse and convert XML layout into layout bindings
        //setContentView(R.layout.activity_main)                //change Activity look to the design in activity_maps.xml
        setContentView(binding.root)                            //change Activity look to the root design of our binding

        initPlaces()
        checkPermissionsForLocation()
    }

    /**
     * Initialize the [PlacesClient] for POI [Place] detail retrieval
     */
    private fun initPlaces() {
        if (!Places.isInitialized())
            Places.initialize(applicationContext, "AIzaSyBQfnnShij4yYUocf4Pa1o-wzgzT6pB2R4");

        placesClient = Places.createClient(this)
    }

    /**
     * Obtains the [SupportMapFragment] and gets notified when the map is ready to be used.
     */
    private fun notifyMapIsReady() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once notified as available by [notifyMapIsReady].
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLng = LatLng(latitude, longitude)
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Current Location")
                .snippet("You are here!")
        )!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

        goToLocation = binding.goToLocation
        goToLocation.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.position, 17f))
        }

        setPOIOnClickPopup()
    }

    /**
     * Sets listeners on the present POI [Marker]s so, when clicked, they call the
     * TODO Finish doc
     */
    private fun setPOIOnClickPopup() {

        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.RATING,
            Place.Field.USER_RATINGS_TOTAL,
            Place.Field.TYPES,
            Place.Field.BUSINESS_STATUS,
            Place.Field.OPENING_HOURS,
            Place.Field.PRICE_LEVEL,
            Place.Field.PHOTO_METADATAS,
            Place.Field.DELIVERY,
            Place.Field.DINE_IN,
            Place.Field.TAKEOUT,
            Place.Field.CURBSIDE_PICKUP
        )

        val token = AutocompleteSessionToken.newInstance()

        mMap.setOnPoiClickListener { poi ->
            showPlacePopup(poi, placeFields, token)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }

        mMap.setOnMapClickListener { latLng ->
            createPersonalMarker(latLng)
        }
    }

    /**
     * TODO Write doc
     */
    private fun createPersonalMarker(latLng: LatLng) {
        val dialogBuilder = AlertDialog.Builder(this)
        val createMarkerView = layoutInflater.inflate(R.layout.create_marker, null)

        placeName = createMarkerView.findViewById(R.id.placeName)
        placeDescription = createMarkerView.findViewById(R.id.placeDescription)
        createButton = createMarkerView.findViewById(R.id.createButton)
        closeButtonMarker = createMarkerView.findViewById(R.id.closeButton)
        errorText = createMarkerView.findViewById(R.id.errorText)
        errorText.visibility = View.GONE

        dialogBuilder.setView(createMarkerView)
        val dialog = dialogBuilder.create()
        dialog.show()

        createButton.setOnClickListener {
            if (placeName.text.isNotEmpty() && placeDescription.text.isNotEmpty()) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(placeName.text.toString())
                        .snippet(placeDescription.text.toString())
                        .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
                )
                dialog.hide()
            }
            else {
                errorText.text = "Please fill both fields"
                errorText.visibility = View.VISIBLE
            }
        }

        closeButtonMarker.setOnClickListener {
            dialog.hide()
        }
    }

    /**
     * Create a [FetchPlaceRequest] in order to retrieve the POI's [Place] details, run said request
     * and create a new popup window to display the location's information using [PopupBuilder] if
     * the request task is successful
     */
    private fun showPlacePopup(poi: PointOfInterest, placeFields: List<Place.Field>, token: AutocompleteSessionToken) {
        val fetchRequest = FetchPlaceRequest.builder(poi.placeId, placeFields).setSessionToken(token).build()
        val task = placesClient.fetchPlace(fetchRequest)
        task.addOnSuccessListener { result ->
            val place = result.place
            val dialogBuilder = AlertDialog.Builder(this)
            val popupView = layoutInflater.inflate(R.layout.popup, null)
            val popupBuilder = PopupBuilder(popupView, place, placesClient, applicationContext, layoutInflater)
            popupBuilder.build()

            dialogBuilder.setView(popupView)
            val dialog = dialogBuilder.create()
            dialog.show()

            closeButton = popupView.findViewById(R.id.closeButton)

            //Hide popup if close button is clicked
            closeButton.setOnClickListener {
                dialog.hide()
            }

        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to retrieve this location's details.", Toast.LENGTH_SHORT).show()
        }
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
            getLastLocation()
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
                getLastLocation()
            }
            else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Retrieves last known location by setting up a [LocationRequest], running it
     * through [FusedLocationProviderClient], receiving updating notifications
     * through [LocationCallback]s that contain [LocationResult]s holding the
     * relevant location information.
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

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
                            notifyMapIsReady()
                        }
                    }
                },
                Looper.getMainLooper()
            )
    }

    /**
     * TODO Write doc
     */
    fun markNewCurrentPosition(latitude: Double, longitude: Double) {
        currentMarker.remove()
        val latLng = LatLng(latitude, longitude)
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Current Location")
                .snippet("You are here!")
        )!!
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }
}