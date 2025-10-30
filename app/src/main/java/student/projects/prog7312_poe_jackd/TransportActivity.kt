package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import java.net.URL
import android.widget.ImageButton

class TransportActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var transportAdapter: TransportAdapter
    private val apiKey = "7b1549d3f4c64d899211928b3713d2dc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transport)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Hamburger menu button
        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Transport search UI
        val locationInput = findViewById<TextInputEditText>(R.id.locationInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)

        // Setup RecyclerView
        transportAdapter = TransportAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transportAdapter

        searchButton.setOnClickListener {
            val location = locationInput.text.toString()
            if (location.isNotEmpty()) {
                getNearbyTransport(location)
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNearbyTransport(location: String) {
        Thread {
            try {
                val geocodeUrl =
                    "https://api.geoapify.com/v1/geocode/search?text=$location&apiKey=$apiKey"
                val response = URL(geocodeUrl).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("features")
                if (features.length() > 0) {
                    val props = features.getJSONObject(0).getJSONObject("properties")
                    val lat = props.getDouble("lat")
                    val lon = props.getDouble("lon")

                    runOnUiThread {
                        fetchNearbyPlaces(lat, lon)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Could not find that location.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun fetchNearbyPlaces(lat: Double, lon: Double) {
        val service = GeoapifyRetrofitClient.instance

        // Valid categories only
        val categories = "public_transport,airport"
        val filter = "circle:$lon,$lat,2000" // 2km radius

        service.getNearbyTransport(categories, filter, apiKey = apiKey)
            .enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(
                    call: Call<GeoapifyResponse>,
                    response: Response<GeoapifyResponse>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body()?.features ?: emptyList()
                        transportAdapter.setItems(results)

                        if (results.isEmpty()) {
                            Toast.makeText(
                                this@TransportActivity,
                                "No nearby transport found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@TransportActivity,
                            "Error ${response.code()}: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    Toast.makeText(
                        this@TransportActivity,
                        "Failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_search -> startActivity(Intent(this, MySearchActivity::class.java))
            R.id.my_suitcase -> startActivity(Intent(this, MySuitcaseActivity::class.java))
            R.id.my_profile -> startActivity(Intent(this, UserProfileActivity::class.java))
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
