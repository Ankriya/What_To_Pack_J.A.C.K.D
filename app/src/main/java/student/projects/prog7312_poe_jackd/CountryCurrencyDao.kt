package student.projects.prog7312_poe_jackd

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CountryCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryCurrencyEntity>)

    @Query("SELECT * FROM country_currency ORDER BY countryName ASC")
    suspend fun getAllCountries(): List<CountryCurrencyEntity>

    @Query("SELECT MAX(lastFetched) FROM country_currency")
    suspend fun getLastFetchTimestamp(): Long?
}