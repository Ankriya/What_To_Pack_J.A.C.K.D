package student.projects.prog7312_poe_jackd

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TravelApiService {
    @GET("api/countries")
    suspend fun getCountriesSuspend(): List<Country>

    @GET("api/currency/convert/{from}/{to}/{amount}")
    fun convertCurrency(
        @Path("from") from: String,
        @Path("to") to: String,
        @Path("amount") amount: Double
    ): Call<CurrencyResponse>
}