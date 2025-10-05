package student.projects.prog7312_poe_jackd

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "RestaurantActivity"

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var menuButton: ImageButton? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private var searchView: SearchView? = null
    private val restaurants = mutableListOf<Properties>()

    // Placing the API key here for testing.
    private val apiKey = "7b1549d3f4c64d899211928b3713d2dc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        try {
            // Find views safely
            drawerLayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.nav_view)
            menuButton = findViewById(R.id.menu_button)
            val rootView = findViewById<android.view.View>(R.id.main)
            recyclerView = findViewById(R.id.restaurant_recycler_view)
            searchView = findViewById(R.id.search_view)

            // Attached navigation listener because nav view exists
            navigationView?.setNavigationItemSelectedListener(this)

            // Menu button opens drawer
            menuButton?.setOnClickListener {
                if (drawerLayout != null) drawerLayout!!.openDrawer(GravityCompat.START)
                else Log.w(TAG, "menu_button clicked but drawerLayout is null")
            }

            // Safe insets handling (suppress lint since types are platform-specific whataflop)
            rootView?.let { v ->
                @SuppressLint("WrongConstant")
                ViewCompat.setOnApplyWindowInsetsListener(v) { view, insets ->
                    val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                    view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }

            // Ensures recyclerView is found
            if (!::recyclerView.isInitialized) {
                Toast.makeText(this, "Recycler view not found in layout (check id)", Toast.LENGTH_LONG).show()
                Log.e(TAG, "recyclerView not found (id mismatch). Aborting init.")
                return
            }

            adapter = RestaurantAdapter(restaurants)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            // safe search view
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val q = query?.trim()
                    if (!q.isNullOrEmpty()) {
                        geocodeAndSearch(q)
                        searchView?.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // no-op
                    return false
                }
            }) ?: run {
                Log.w(TAG, "searchView is null - cannot attach listener (check layout id)")
            }

        } catch (ex: Exception) {
            Log.e(TAG, "Error during onCreate", ex)
            Toast.makeText(this, "Failed to initialize Restaurant screen: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun geocodeAndSearch(query: String) {
        GeoapifyRetrofitClient.instance.geocode(query, apiKey = apiKey)
            .enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val features = response.body()!!.features
                        if (features.isNotEmpty()) {
                            val props = features[0].properties
                            performPlacesSearch(props.lat, props.lon)
                        } else {
                            Toast.makeText(this@RestaurantActivity, "Location not found for \"$query\"", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RestaurantActivity, "Geocode failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    Toast.makeText(this@RestaurantActivity, "Geocode error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun performPlacesSearch(lat: Double, lon: Double) {
        val radius = 5000
        val filter = "circle:$lon,$lat,$radius"
        val bias = "proximity:$lon,$lat"

        GeoapifyRetrofitClient.instance.getRestaurantsByLocation(filter = filter, bias = bias, apiKey = apiKey)
            .enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        restaurants.clear()
                        response.body()!!.features.forEach { restaurants.add(it.properties) }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@RestaurantActivity, "No restaurants found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    Toast.makeText(this@RestaurantActivity, "Places error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_search -> startActivity(Intent(this, MySearchActivity::class.java))
            R.id.my_suitcase -> startActivity(Intent(this, MySuitcaseActivity::class.java))
            R.id.my_profile -> startActivity(Intent(this, UserProfileActivity::class.java))
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
