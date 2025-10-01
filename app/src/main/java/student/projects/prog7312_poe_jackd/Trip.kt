package student.projects.prog7312_poe_jackd

data class Trip(
    val id: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val time: String = "",
    val airport: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = ""
)