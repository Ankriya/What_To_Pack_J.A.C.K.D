package student.projects.prog7312_poe_jackd

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyApiService {

    // Geocoding: convert text -> coordinates. this is for the search functionality.
    @GET("v1/geocode/search")
    fun geocode(
        @Query("text") text: String,
        @Query("limit") limit: Int = 1,
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>

    // Places search by location (radius) and category=catering. this is for the search request.
    @GET("v2/places?categories=catering")
    fun getRestaurantsByLocation(
        @Query("filter") filter: String, // "circle:lon,lat,radius"
        @Query("bias") bias: String,     // "proximity:lon,lat"
        @Query("limit") limit: Int = 15,
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>
}
