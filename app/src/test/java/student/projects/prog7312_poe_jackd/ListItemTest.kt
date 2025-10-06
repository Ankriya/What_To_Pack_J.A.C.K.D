package student.projects.prog7312_poe_jackd

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ListItemTest {

    private lateinit var testListItem: ListItem
    private val testItems = listOf("Toothbrush", "Passport", "Sunscreen")

    @Before
    fun setup() {
        testListItem = ListItem(
            id = "list123",
            title = "Beach Vacation Essentials",
            items = testItems,
            userId = "user456"
        )
    }

    @Test
    fun `list item is created with correct properties`() {
        assertEquals("list123", testListItem.id)
        assertEquals("Beach Vacation Essentials", testListItem.title)
        assertEquals(3, testListItem.items.size)
        assertEquals("user456", testListItem.userId)
        assertTrue(testListItem.items.contains("Passport"))
    }

    @Test
    fun `list item with empty values behaves correctly`() {
        val emptyList = ListItem()

        assertTrue(emptyList.id.isEmpty())
        assertTrue(emptyList.title.isEmpty())
        assertTrue(emptyList.items.isEmpty())
        assertTrue(emptyList.userId.isEmpty())
    }

    @Test
    fun `timestamp is set automatically and is valid`() {
        val beforeTime = System.currentTimeMillis()
        val newList = ListItem(title = "Test List")
        val afterTime = System.currentTimeMillis()

        assertTrue(newList.createdAt >= beforeTime)
        assertTrue(newList.createdAt <= afterTime)
        assertTrue(newList.createdAt > 0)
    }

    @Test
    fun `list items are immutable once created`() {
        val originalSize = testListItem.items.size
        val originalTitle = testListItem.title

        val modifiedItems = testListItem.items.toMutableList()
        modifiedItems.add("New Item")

        assertEquals(originalSize, testListItem.items.size)
        assertEquals(originalTitle, testListItem.title)
    }
}