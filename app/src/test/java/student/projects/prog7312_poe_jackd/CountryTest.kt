package student.projects.prog7312_poe_jackd

import org.junit.Assert.*
import org.junit.Test

class CountryTest {

    @Test
    fun `country object is created with correct properties`() {
        val country = Country(
            name = "South Africa",
            code = "ZA",
            currency = "ZAR"
        )

        assertEquals("South Africa", country.name)
        assertEquals("ZA", country.code)
        assertEquals("ZAR", country.currency)
    }

    @Test
    fun `country properties are not null`() {
        val country = Country(
            name = "United Kingdom",
            code = "GB",
            currency = "GBP"
        )

        assertNotNull(country.name)
        assertNotNull(country.code)
        assertNotNull(country.currency)
    }

    @Test
    fun `multiple countries can be created with different currencies`() {
        val southAfrica = Country("South Africa", "ZA", "ZAR")
        val usa = Country("United States", "US", "USD")
        val japan = Country("Japan", "JP", "JPY")

        assertNotEquals(southAfrica.currency, usa.currency)
        assertNotEquals(usa.currency, japan.currency)
        assertEquals(2, southAfrica.code.length)
        assertEquals(2, usa.code.length)
    }

    @Test
    fun `country with empty values is valid but empty`() {
        val emptyCountry = Country("", "", "")

        assertTrue(emptyCountry.name.isEmpty())
        assertTrue(emptyCountry.code.isEmpty())
        assertTrue(emptyCountry.currency.isEmpty())
    }

    @Test
    fun `country code is case sensitive`() {
        val country1 = Country("Test", "ZA", "ZAR")
        val country2 = Country("Test", "za", "ZAR")

        assertNotEquals(country1.code, country2.code)
    }
}