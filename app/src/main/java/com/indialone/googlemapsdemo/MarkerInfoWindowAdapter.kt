package com.indialone.googlemapsdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.indialone.googlemapsdemo.place.Place

class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker?): View? {
        // get tag
        val place = marker?.tag as? Place ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.marker_info_content, null)

        val name = view.findViewById<TextView>(R.id.text_view_title)
        name.text = place.name

        val addresss = view.findViewById<TextView>(R.id.text_view_address)
        addresss.text = place.address

        val rating = view.findViewById<TextView>(R.id.text_view_rating)
        rating.text = "Rating: %.2f".format(place.rating)

        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}