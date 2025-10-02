package student.projects.prog7312_poe_jackd

data class CurrencyResponse(
    val from: String,
    val to: String,
    val amount: Double,
    val rate: Double,
    val result: String
)