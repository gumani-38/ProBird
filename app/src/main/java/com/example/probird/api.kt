package com.example.api
import com.example.probird.IEbird
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val EBIRD_URL = "https://api.ebird.org"
const  val API_BASE_URL: String = "https://api.mapbox.com"
object api {




    var routeBuilder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(IEbird::class.java)
    val eBirdTaxonomicRetrofit =  Retrofit.Builder().baseUrl(EBIRD_URL).addConverterFactory(GsonConverterFactory.create()).build().create(IEbird::class.java)
    val eBirdNearByHotspotRetrofit =  Retrofit.Builder().baseUrl(EBIRD_URL).addConverterFactory(GsonConverterFactory.create()).build().create(IEbird::class.java)
}