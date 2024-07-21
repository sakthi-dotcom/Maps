package com.example.hepto.model


data class DirectionsResponse(
    val status: String,
    val routes: List<Route>
)
