package com.example.probird

import com.example.probird.models.BirdTaxonomy
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IEbird {
    @GET("/v2/ref/taxonomy/ebird")
    suspend fun getExploreBird(@Query("key") key:String,@Query("fmt") fmt: String, @Query("version") version: String): Response<BirdTaxonomy>
}