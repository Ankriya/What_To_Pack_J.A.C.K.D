package student.projects.prog7312_poe_jackd

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoapifyRetrofitClient {
    private const val BASE_URL = "https://api.geoapify.com/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // logs request & response
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val instance: GeoapifyApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // use the logging client
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GeoapifyApiService::class.java)
    }
}
