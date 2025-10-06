package student.projects.prog7312_poe_jackd

import org.junit.Assert.*
import org.junit.Test

class TripTest {

    @Test
    fun `trip creation with all valid data`() {
        val trip = Trip(
            id = "trip789",
            fromLocation = "Johannesburg",
            toLocation = "Cape Town",
            time = "14:30",
            airport = "OR Tambo International",
            userId = "user123"
        )

        assertEquals("trip789", trip.id)
        assertEquals("Johannesburg", trip.fromLocation)
        assertEquals("Cape Town", trip.toLocation)
        assertEquals("14:30", trip.time)
        assertEquals("OR Tambo International", trip.airport)
        assertEquals("user123", trip.userId)
    }

    @Test
    fun `default trip values are empty strings`() {
        val defaultTrip = Trip()

        assertEquals("", defaultTrip.id)
        assertEquals("", defaultTrip.fromLocation)
        assertEquals("", defaultTrip.toLocation)
        assertEquals("", defaultTrip.time)
        assertEquals("", defaultTrip.airport)
        assertEquals("", defaultTrip.userId)
    }

    @Test
    fun `trip timestamp is automatically generated`() {
        val trip1 = Trip(fromLocation = "City A")
        Thread.sleep(10) // Small delay
        val trip2 = Trip(fromLocation = "City B")

        assertTrue(trip1.createdAt > 0)
        assertTrue(trip2.createdAt > 0)
        assertTrue(trip2.createdAt >= trip1.createdAt)
    }

    @Test
    fun `trip with international locations`() {
        val internationalTrip = Trip(
            fromLocation = "New York",
            toLocation = "Tokyo",
            time = "08:45",
            airport = "JFK International"
        )

        assertNotNull(internationalTrip.fromLocation)
        assertNotNull(internationalTrip.toLocation)
        assertFalse(internationalTrip.fromLocation.isEmpty())
        assertFalse(internationalTrip.toLocation.isEmpty())
    }
}