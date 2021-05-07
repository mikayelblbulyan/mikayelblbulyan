package com.app.jsinnovations.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.jsinnovations.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


class ContactUsFragment : Fragment(), OnMapReadyCallback {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = activity?.findViewById<TextView>(R.id.title)
        title?.text = getString(R.string.contact_us)
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(40.174553, 44.522422)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val myPosition = CameraPosition.Builder()
            .target(latLng).zoom(17F).bearing(90F).tilt(30F).build()
        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(myPosition))
    }
}