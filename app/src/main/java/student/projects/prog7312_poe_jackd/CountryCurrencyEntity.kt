package student.projects.prog7312_poe_jackd

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_currency")
data class CountryCurrencyEntity (
    val countryName: String,
    @PrimaryKey(autoGenerate = false)
    val countryCode: String,
    val currencyCode: String,
    val lastFetched: Long = System.currentTimeMillis()
)

fun List<Country>.toEntityList(): List<CountryCurrencyEntity>{
    return  this.map { apiCountry ->
        CountryCurrencyEntity(
            countryName = apiCountry.name,
            countryCode = apiCountry.code,
            currencyCode = apiCountry.currency
        )
    }
}