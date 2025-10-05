package student.projects.prog7312_poe_jackd

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val name: String?,
    val address_line2: String?,
    val lon: Double,
    val lat: Double,
    val datasource: DataSource?
)

data class DataSource(
    var raw: RawData?
)

data class RawData(
    val amenity: String?
)
