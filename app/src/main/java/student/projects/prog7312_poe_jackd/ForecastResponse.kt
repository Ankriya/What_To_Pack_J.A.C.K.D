package student.projects.prog7312_poe_jackd

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class City(val name: String)

data class ForecastItem(
    val dt_txt: String,       // date and time string
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

