package com.example.hepto.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hepto.network.ApiService
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MapsViewModel : ViewModel() {

    private val _polylineOptions = MutableLiveData<PolylineOptions>()
    val polylineOptions: LiveData<PolylineOptions> get() = _polylineOptions

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun fetchDirections(origin: String, destination: String, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getDirections(origin, destination, apiKey)


                Log.d("Maps response", "API Response: ${response.toString()}")


                if (response.routes.isNotEmpty()) {
                    val route = response.routes[0]
                    val polylineOptions = PolylineOptions()

                    val points = route.overviewPolyline?.points
                    if (points != null) {
                        val decodedPath = PolyUtil.decode(points)
                        polylineOptions.addAll(decodedPath)
                        polylineOptions.color(Color.BLUE)
                        polylineOptions.width(10f)


                        withContext(Dispatchers.Main) {
                            _polylineOptions.value = polylineOptions
                        }
                        Log.d("Maps response", "Polyline options set successfully")
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("MapsFragment", "No points found in the response")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("MapsFragment", "No routes found in the response")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Log.e("MapsFragment", "HTTP error: ${e.message()}")
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Log.e("MapsFragment", "Network error: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MapsFragment", "Unexpected error: ${e.message}")
                }
            }
        }
    }
}
