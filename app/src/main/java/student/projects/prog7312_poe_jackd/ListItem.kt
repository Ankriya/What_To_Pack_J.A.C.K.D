package student.projects.prog7312_poe_jackd

class ListItem(
    val id: String = "",
    val title: String = "",
    val items: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = ""

)