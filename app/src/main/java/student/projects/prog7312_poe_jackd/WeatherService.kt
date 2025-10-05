package student.projects.prog7312_poe_jackd

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(val temp: Double, val humidity: Int)
data class Weather(val description: String)
data class Wind(val speed: Double)

interface WeatherService {
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>

}