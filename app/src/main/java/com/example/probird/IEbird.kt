package com.example.probird

import com.example.probird.models.BirdTaxonomy
import com.example.probird.models.DirectionResponse
import com.example.probird.models.EbirdHotspot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IEbird {
    @GET("/v2/ref/taxonomy/ebird")
    suspend fun getExploreBird(@Query("key") key:String,@Query("fmt") fmt: String, @Query("version") version: String): Response<BirdTaxonomy>
   @GET("/v2/ref/hotspot/geo")
   suspend fun getNearByHotspot(@Query("key") key:String,@Query("fmt") fmt: String, @Query("lat") lat: Double, @Query("lng") lng:Double): Response<EbirdHotspot>
    @GET("/directions/v5/mapbox/driving/{startLon},{startLat};{endLon},{endLat}")
    suspend fun getDirections
                (@Path("startLat") startLat:String,
                 @Path("startLon") startLot:String,
                 @Path("endLat") endLat:String,
                 @Path("endLon") endLot:String,
                 @Query("access_token") token:String,
                 @Query("geometries") geo:String = "geojson"
    ): Response<DirectionResponse>
}