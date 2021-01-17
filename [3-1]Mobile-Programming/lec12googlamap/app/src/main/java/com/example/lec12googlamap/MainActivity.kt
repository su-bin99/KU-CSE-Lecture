package com.example.lec12googlamap

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var fusedLocationClient: FusedLocationProviderClient?= null
    var locationCallback : LocationCallback ?= null
    var locationRequest :LocationRequest?= null

    lateinit var googleMap:GoogleMap
    var loc = LatLng(37.554752, 126.970631) //객체 생성
    val arrLoc = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
        //initSpinner()
        //initLocation()
    }

    fun initLocation(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            getuserlocation()
            startLocationUpdates()
            initMap()
        }else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), 100)
                //request코드는 100번!!
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]== PackageManager.PERMISSION_GRANTED){
                getuserlocation()
                startLocationUpdates()
                initMap()
            }else{
                Toast.makeText(this, "위치정보 제공을 하셔야 합니다.", Toast.LENGTH_SHORT).show()
                initMap()
            }
        }
    }

    fun startLocationUpdates(){//location정보 갱신
        locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object :LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?:return
                for(location in locationResult.locations){
                    loc = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
                }
            }
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates(){
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    fun getuserlocation(){
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient?.lastLocation?.addOnSuccessListener{
            loc = LatLng(it.latitude, it.longitude)
        }
    }

    fun initMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            googleMap = it
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
            googleMap.setMinZoomPreference(10.0f)
            googleMap.setMaxZoomPreference(18.0f)
            val options = MarkerOptions()
            options.position(loc)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            options.title("역")
            options.snippet("서울역")
            val mk1 = googleMap.addMarker(options)
            mk1.showInfoWindow()

            initMapListener()
        }
    }

    fun initMapListener(){
        googleMap.setOnMapClickListener {
            googleMap.clear()
            arrLoc.add(it)
            val options = MarkerOptions()
            options.position(it)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            googleMap.addMarker(options)
            //val lineOptions= PolylineOptions().color(Color.GREEN).addAll(arrLoc)
            // googleMap.addPolyline(lineOptions)
            val option2 = PolygonOptions().fillColor(Color.argb(100, 255, 255, 0))
                .strokeColor(Color.GREEN).addAll(arrLoc)
            googleMap.addPolygon(option2)
        }
    }

    fun initSpinner(){
        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item, ArrayList<String>())
        adapter.add("Hybrid")
        adapter.add("Normal")
        adapter.add("Satellite")
        adapter.add("Terrain")
        spinner.adapter = adapter
        spinner.setSelection(1)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0->{
                        googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                    }
                    1->{
                        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                    }
                    2->{
                        googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    }
                    3->{
                        googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    }
                }
            }
        }
    }



}