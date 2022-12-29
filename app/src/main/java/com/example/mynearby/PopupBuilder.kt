package com.example.mynearby

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import kotlin.math.roundToInt


/**
 * Class tasked with setting up the place details' pop-up window [View] elements in the respective
 * layout with the input place information.
 *
 * @param context main application's context
 * @param placesClient place information retrieving client
 * @param place pop-up window's information respective place
 * @param contactPopupView layout to format with place information
 * @author Gonçalo Oliveira
 */
class PopupBuilder(private val context: Context, private val placesClient: PlacesClient, private val place: Place, private val contactPopupView : View) {

    //popup fields
    private lateinit var popupTitle: TextView
    private lateinit var popupTypes: TextView
    private lateinit var rating: TextView
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var ratingsAmount: TextView
    private lateinit var priceDot: TextView
    private lateinit var price: TextView
    private lateinit var popupAddress: TextView
    private lateinit var info1: TextView
    private lateinit var info1dot: TextView
    private lateinit var info2: TextView
    private lateinit var info2dot: TextView
    private lateinit var info3: TextView
    private lateinit var scheduleState: TextView
    private lateinit var scheduleDot: TextView
    private lateinit var scheduleTime: TextView
    private lateinit var popupPhoneNumber: TextView
    private lateinit var imagesScrollView: HorizontalScrollView
    private lateinit var imagesLayout: LinearLayout

    fun build() {

        setViews()

        setName()
        setTypes()
        setRating()
        setPrice()
        setAddress()
        setDiningInfo()
        setBusinessStatus()
        setWeekDayHours()
        setPhoneNumber()
        setPhotos()
    }

    private fun setViews() {
        popupTitle = contactPopupView.findViewById(R.id.popupTitle)
        popupTypes = contactPopupView.findViewById(R.id.popupTypes)
        rating = contactPopupView.findViewById(R.id.rating)
        star1 = contactPopupView.findViewById(R.id.star1)
        star2 = contactPopupView.findViewById(R.id.star2)
        star3 = contactPopupView.findViewById(R.id.star3)
        star4 = contactPopupView.findViewById(R.id.star4)
        star5 = contactPopupView.findViewById(R.id.star5)
        ratingsAmount = contactPopupView.findViewById(R.id.ratingsAmount)
        priceDot = contactPopupView.findViewById(R.id.priceDot)
        price = contactPopupView.findViewById(R.id.price)
        popupAddress = contactPopupView.findViewById(R.id.popupAddress)
        info1 = contactPopupView.findViewById(R.id.info1)
        info1dot = contactPopupView.findViewById(R.id.info1dot)
        info2 = contactPopupView.findViewById(R.id.info2)
        info2dot = contactPopupView.findViewById(R.id.info2dot)
        info3 = contactPopupView.findViewById(R.id.info3)
        scheduleState = contactPopupView.findViewById(R.id.scheduleState)
        scheduleDot = contactPopupView.findViewById(R.id.scheduleDot)
        scheduleTime = contactPopupView.findViewById(R.id.scheduleTime)
        popupPhoneNumber = contactPopupView.findViewById(R.id.popupPhoneNumber)
        imagesScrollView = contactPopupView.findViewById(R.id.imagesScrollView)
        imagesLayout = contactPopupView.findViewById(R.id.imagesLayout)
    }

    private fun setName() {
        if (place.name != null)
            popupTitle.text = place.name
        else
            popupTitle.visibility = View.GONE
    }

    private fun setTypes() {
        if (place.types != null){

            val types = place.types
            var typeString = ""

            for (type in types!!) {

                if (type != Place.Type.POINT_OF_INTEREST) {

                    val typeFormatted = typeToTitleCase(type.toString())

                    typeString = if (typeString == "")
                        typeFormatted
                    else
                        "$typeString, $typeFormatted"
                }
            }

            popupTypes.text = typeString
        }
        else {
            popupTypes.visibility = View.GONE
        }
    }

    private fun setRating(){

        if (place.rating != null) {
            rating.text = place.rating?.toString()

            setStars()
            setRatingsTotal()
        }
        else {
            rating.visibility = View.GONE
            star1.visibility = View.GONE
            star2.visibility = View.GONE
            star3.visibility = View.GONE
            star4.visibility = View.GONE
            star5.visibility = View.GONE
            ratingsAmount.visibility = View.GONE
            priceDot.visibility = View.GONE
        }
    }

    private fun setStars() {
        val stars = listOf(star1, star2, star3, star4, star5)

        val rating = place.rating

        if (rating != null) {
            for (i in 0 until rating.roundToInt()) {
                stars[i].setColorFilter(Color.argb(255, 15,185,212))
            }
        }
    }

    private fun setRatingsTotal() {
        if (place.userRatingsTotal != null) {
            val ratingsAmountText = "(" + place.userRatingsTotal + ")"
            ratingsAmount.text = ratingsAmountText
        }
        else
            ratingsAmount.visibility = View.GONE
    }

    private fun setPrice() {

        val priceLevel = place.priceLevel

        if (priceLevel != null) {
            when (priceLevel) {
                0 -> {
                    priceDot.visibility = View.GONE
                    price.visibility = View.GONE
                }
                1 -> price.text = context.getString(R.string.price_l1)
                2 -> price.text = context.getString(R.string.price_l2)
                3 -> price.text = context.getString(R.string.price_l3)
                4 -> price.text = context.getString(R.string.price_l4)
            }
        }
        else {
            priceDot.visibility = View.GONE
            price.visibility = View.GONE
        }
    }

    private fun setAddress() {
        if (place.address != null)
            popupAddress.text = place.address
        else
            popupAddress.visibility = View.GONE
    }

    private fun setDiningInfo() {
        val dineInfo = mutableListOf<Pair<String,Place.BooleanPlaceAttributeValue>>()

        if(place.dineIn != Place.BooleanPlaceAttributeValue.UNKNOWN && place.dineIn != Place.BooleanPlaceAttributeValue.FALSE)
            dineInfo.add(Pair("Dine In", place.dineIn))

        if(place.delivery != Place.BooleanPlaceAttributeValue.UNKNOWN && place.delivery != Place.BooleanPlaceAttributeValue.FALSE)
            dineInfo.add(Pair("Delivery", place.delivery))

        if(place.takeout != Place.BooleanPlaceAttributeValue.UNKNOWN && place.takeout != Place.BooleanPlaceAttributeValue.FALSE)
            dineInfo.add(Pair("Takeout", place.takeout))

        when (dineInfo.size) {
            0 -> {
                info1.visibility = View.GONE
                info1dot.visibility = View.GONE
                info2.visibility = View.GONE
                info2dot.visibility = View.GONE
                info3.visibility = View.GONE
            }
            1 -> {
                info1.visibility = View.GONE
                info1dot.visibility = View.GONE
                info2.visibility = View.GONE
                info2dot.visibility = View.GONE
                info3.text = dineInfo[0].first
            }
            2 -> {
                info1.visibility = View.GONE
                info1dot.visibility = View.GONE
                info2.text = dineInfo[0].first
                info3.text = dineInfo[1].first
            }
            else -> {
                info1.text = dineInfo[0].first
                info2.text = dineInfo[1].first
                info3.text = dineInfo[2].first
            }
        }
    }

    private fun setBusinessStatus() {

        val businessStatus = place.businessStatus

        if (businessStatus != null) {
            when (businessStatus) {
                Place.BusinessStatus.CLOSED_PERMANENTLY -> {
                    scheduleState.text = context.getString(R.string.closed_permanently)
                    scheduleState.setTextColor(Color.argb(255, 255,76,76))
                }
                Place.BusinessStatus.CLOSED_TEMPORARILY -> {
                    scheduleState.text = context.getString(R.string.closed)
                    scheduleState.setTextColor(Color.argb(255, 255,191,0))
                }
                Place.BusinessStatus.OPERATIONAL -> {
                    scheduleState.text = context.getString(R.string.open)
                    scheduleState.setTextColor(Color.argb(255, 52,191,73))
                }
            }
        }
        else {
            scheduleState.visibility = View.GONE
            scheduleDot.visibility = View.GONE
        }
    }

    private fun setWeekDayHours() {

        val openingHours = place.openingHours

        if (openingHours != null && place.businessStatus != Place.BusinessStatus.CLOSED_PERMANENTLY) {

            val calendar = Calendar.getInstance()
            var dayOfWeek = 0
            var excludeText = ""

            when (calendar[Calendar.DAY_OF_WEEK]) {
                Calendar.MONDAY -> {
                    dayOfWeek = 0
                    excludeText = "Monday: "
                }
                Calendar.TUESDAY -> {
                    dayOfWeek = 1
                    excludeText = "Tuesday: "
                }
                Calendar.WEDNESDAY -> {
                    dayOfWeek = 2
                    excludeText = "Wednesday: "
                }
                Calendar.THURSDAY -> {
                    dayOfWeek = 3
                    excludeText = "Thursday: "
                }
                Calendar.FRIDAY -> {
                    dayOfWeek = 4
                    excludeText = "Friday: "
                }
                Calendar.SATURDAY -> {
                    dayOfWeek = 5
                    excludeText = "Saturday: "
                }
                Calendar.SUNDAY -> {
                    dayOfWeek = 6
                    excludeText = "Sunday: "
                }
            }

            scheduleTime.text = openingHours.weekdayText[dayOfWeek].removePrefix(excludeText)
        }
        else {
            scheduleDot.visibility = View.GONE
            scheduleTime.visibility = View.GONE
        }
    }

    private fun setPhoneNumber() {
        if (place.phoneNumber != null)
            popupPhoneNumber.text = place.phoneNumber
        else
            popupPhoneNumber.visibility = View.GONE
    }

    private fun setPhotos() {

        val photoMetadatas = place.photoMetadatas

        if (photoMetadatas != null) {

            for (photoMetadata in photoMetadatas) {

                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()

                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->

                        val bitmap = fetchPhotoResponse.bitmap

                        val imageView = ImageView(context)
                        imageView.adjustViewBounds = true
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        imageView.layoutParams = layoutParams
                        imageView.setImageBitmap(bitmap)

                        // Add ImageView to LinearLayout
                        imagesLayout.addView(imageView)

                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            val statusCode = exception.statusCode
                            Log.e(TAG, "Error occurred while fetching photo metadata! Status Code: $statusCode")
                        }
                    }
            }
        }
        else {
            imagesScrollView.visibility = View.GONE
        }
    }

    private fun typeToTitleCase(string: String): String {
        val typeLowercase = string.lowercase()
        val substrings = typeLowercase.split("_")
        var typeTitleCase = ""

        for (substring in substrings) {

            var fSubstring = substring

            if (substring.length > 2) {
                fSubstring = substring.replaceFirstChar {
                    it.uppercase()
                }
            }

            typeTitleCase = if (typeTitleCase == "")
                fSubstring
            else
                "$typeTitleCase $fSubstring"
        }
        return typeTitleCase
    }
}