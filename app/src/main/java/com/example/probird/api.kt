package com.example.api
import com.example.probird.IEbird
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val EBIRD_URL = "https://api.ebird.org"
object api {
    val eBirdTaxonomicRetrofit =  Retrofit.Builder().baseUrl(EBIRD_URL).addConverterFactory(GsonConverterFactory.create()).build().create(IEbird::class.java)
}