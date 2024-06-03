package com.example.recylerv.message

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.LocationManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.Flow


class SendMessage(var context: Context) {

    private fun getLocation(phNumberList: List<String>){
        Log.d("getLocation", "Function getting called!!")
        // Code for getting location
        val locationListener =
            LocationListener { location ->
                try {
                    Log.d("LatLong", "Reached LatLong")
                    val myLatitude = location.latitude.toString()
                    val myLongitude = location.longitude.toString()
                    val message =
                        """
                      I need help, please try and reach out to me ASAP. Below is my location:
                      http://maps.google.com/?q=$myLatitude,$myLongitude
                      """.trimIndent()

                    val smsManager = SmsManager.getDefault()

                    for (phNum in phNumberList){
                        smsManager.sendTextMessage(phNum, null, message, null, null)
                    }
                    Toast.makeText(context, "Messages Sent!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Log.d("LatLong","LatLong not found")
                    e.printStackTrace()
                }
            }
        // Requesting location

        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                100,
                0f,
                locationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                100,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    fun onBtnClick(phNumberList: List<String>){
        if(phNumberList.isEmpty()){
            return
        }
        getLocation(phNumberList)
    }
}