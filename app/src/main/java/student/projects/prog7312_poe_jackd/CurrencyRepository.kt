package student.projects.prog7312_poe_jackd

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.util.concurrent.TimeUnit

class CurrencyRepository(
    private val apiService: TravelApiService,
    private val dao: CountryCurrencyDao,
    private val context: Context
) {
    //cache data is considered stale after 7 days
    private val STALE_THRESHOLD_MS = TimeUnit.DAYS.toMillis(7)

    private fun isOnline(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    suspend fun getCountryCurrencies(): List<CountryCurrencyEntity> {
        val cachedData = dao.getAllCountries()
        val isConnected = isOnline(context)
        val lastFetchedTime = dao.getLastFetchTimestamp() ?: 0L
        val isStale = (System.currentTimeMillis() - lastFetchedTime) > STALE_THRESHOLD_MS

        // Strategy: If online AND (cache is empty OR cache is stale), try to fetch new data
        if (isConnected && (cachedData.isEmpty() || isStale)) {
            try {
                // Call the new Coroutine-friendly API method
                val apiData = apiService.getCountriesSuspend()
                val entities = apiData.toEntityList()

                // Save fresh data to Room
                dao.insertAll(entities)
                return entities // Return the fresh data
            } catch (e: Exception) {
                // API call failed (e.g., server down). Fall back to cache.
                e.printStackTrace()
                if (cachedData.isNotEmpty()) {
                    // Show stale cache data
                    return cachedData
                }
                // If cache is empty AND API failed, throw up.
                throw IllegalStateException("No network connection and no cached data available.")
            }
        }

        // If offline OR cache is fresh, return the cached data immediately
        if (cachedData.isNotEmpty()) {
            return cachedData
        }

        // Final fallback (first run, no network)
        throw IllegalStateException("No network connection and no cached data available.")
    }
}