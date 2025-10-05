package student.projects.prog7312_poe_jackd

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoapifyRetrofitClient {
    private const val BASE_URL = "https://api.geoapify.com/"

    val instance: GeoapifyApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GeoapifyApiService::class.java)
    }
}
