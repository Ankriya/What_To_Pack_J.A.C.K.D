package student.projects.prog7312_poe_jackd

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "EventsActivity"

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var menuButton: ImageButton? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventVenueAdapter
    private var searchView: SearchView? = null
    private val venues = mutableListOf<Properties>()

    private val apiKey = "7b1549d3f4c64d899211928b3713d2dc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        try {
            drawerLayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.nav_view)
            menuButton = findViewById(R.id.menu_button)
            recyclerView = findViewById(R.id.events_recycler_view)
            searchView = findViewById(R.id.search_view)

            navigationView?.setNavigationItemSelectedListener(this)

            menuButton?.setOnClickListener {
                drawerLayout?.openDrawer(GravityCompat.START)
            }

            val rootView = findViewById<android.view.View>(R.id.main)
            rootView?.let { v ->
                @SuppressLint("WrongConstant")
                ViewCompat.setOnApplyWindowInsetsListener(v) { view, insets ->
                    val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                    view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }

            adapter = EventVenueAdapter(venues)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            // Same listener as RestaurantActivity
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
                    return false
                }
            }) ?: Log.w(TAG, "searchView is null - check layout id")

        } catch (ex: Exception) {
            Log.e(TAG, "Error during onCreate", ex)
            Toast.makeText(this, "Failed to load Events page: ${ex.message}", Toast.LENGTH_LONG).show()
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
                            performVenueSearch(props.lat, props.lon)
                        } else {
                            Toast.makeText(this@EventsActivity, "Location not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EventsActivity, "Geocode failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    Toast.makeText(this@EventsActivity, "Geocode error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun performVenueSearch(lat: Double, lon: Double) {
        val radius = 20000
        val filter = "circle:$lon,$lat,$radius"
        val bias = "proximity:$lon,$lat"

        val categories = "entertainment"

        GeoapifyRetrofitClient.instance.getPlaces(categories, filter, bias, 20, apiKey)
            .enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        venues.clear()
                        response.body()!!.features.forEach { venues.add(it.properties) }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@EventsActivity, "No venues found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    Toast.makeText(this@EventsActivity, "Places error: ${t.message}", Toast.LENGTH_LONG).show()
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
