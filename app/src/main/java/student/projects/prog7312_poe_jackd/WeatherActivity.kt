package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private val apiKey = "5a42ddd44e6586291b81ff7b5ba82d5f" // OpenWeather API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Hamburger menu
        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Weather UI elements
        val editCity = findViewById<EditText>(R.id.editCity)
        val btnGetWeather = findViewById<Button>(R.id.btnGetWeather)
        val txtLocation = findViewById<TextView>(R.id.txtLocation)
        val txtTemperature = findViewById<TextView>(R.id.txtTemperature)
        val txtHumidity = findViewById<TextView>(R.id.txtHumidity)
        val txtWind = findViewById<TextView>(R.id.txtWind)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)

        val forecastRecyclerView = findViewById<RecyclerView>(R.id.recyclerForecast)
        forecastRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch weather when button is clicked
        btnGetWeather.setOnClickListener {
            val city = editCity.text.toString().trim()

            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        WeatherRetrofitInstance.api.getForecast(city, apiKey)
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val forecastData = response.body()!!.list

                        if (forecastData.isNotEmpty()) {
                            // Display today’s weather in the card (first item)
                            val today = forecastData[0]
                            txtLocation.text = "🌍 $city"
                            txtTemperature.text = "🌡 ${today.main.temp} °C"
                            txtHumidity.text = "💧 Humidity: ${today.main.humidity}%"
                            txtWind.text = "🌬 Wind: ${today.wind.speed} m/s"
                            txtCondition.text =
                                "☁ ${today.weather[0].description.replaceFirstChar { it.uppercase() }}"

                            // Filter one forecast per day (midday 12:00:00)
                            val dailyForecast = forecastData.filter { it.dt_txt.contains("12:00:00") }

                            // Setup RecyclerView with daily forecast
                            val adapter = ForecastAdapter(dailyForecast)
                            forecastRecyclerView.adapter = adapter
                        } else {
                            clearWeatherDisplay(txtLocation, txtTemperature, txtHumidity, txtWind, txtCondition)
                        }

                    } else {
                        clearWeatherDisplay(txtLocation, txtTemperature, txtHumidity, txtWind, txtCondition)
                    }
                } catch (e: Exception) {
                    txtLocation.text = "Error: ${e.message}"
                    clearWeatherDisplay(txtLocation, txtTemperature, txtHumidity, txtWind, txtCondition)
                }
            }
        }
    }

    private fun clearWeatherDisplay(
        txtLocation: TextView,
        txtTemperature: TextView,
        txtHumidity: TextView,
        txtWind: TextView,
        txtCondition: TextView
    ) {
        txtTemperature.text = ""
        txtHumidity.text = ""
        txtWind.text = ""
        txtCondition.text = ""
    }

    // Navigation Drawer handling
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_search -> startActivity(Intent(this, MySearchActivity::class.java))
            R.id.my_suitcase -> startActivity(Intent(this, MySuitcaseActivity::class.java))
            R.id.my_profile -> startActivity(Intent(this, UserProfileActivity::class.java))
            R.id.my_settings -> startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
