package student.projects.prog7312_poe_jackd

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripDetailsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currencyRepository: CurrencyRepository // Repository instance

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fromInput: AutoCompleteTextView
    private lateinit var toInput: AutoCompleteTextView
    private lateinit var timeInput: EditText
    private lateinit var airportInput: EditText

    private var countryEntities = listOf<CountryCurrencyEntity>() // List now uses the Room entity

    // Helper function for connectivity check (needed for the Toast feedback)
    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Repository Initialization
        val apiService = RetrofitClient.instance
        val currencyDao = CurrencyDatabase.getDatabase(applicationContext).countryCurrencyDao()
        currencyRepository = CurrencyRepository(apiService, currencyDao, applicationContext)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        fromInput = findViewById(R.id.FromInput)
        toInput = findViewById(R.id.ToInput)
        timeInput = findViewById(R.id.TimeInput)
        airportInput = findViewById(R.id.AirportInput)

        loadCountries()

        findViewById<Button>(R.id.SubmitBtn).setOnClickListener {
            saveTripToFirestore()
        }

        findViewById<Button>(R.id.ViewTripsBtn).setOnClickListener {
            startActivity(Intent(this, ViewTripsActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //Uses Coroutines and the Repository for offline-first data fetching
    private fun loadCountries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entities = currencyRepository.getCountryCurrencies()

                withContext(Dispatchers.Main) {
                    countryEntities = entities
                    setupCountryDropdowns()

                    if (entities.isEmpty()) {
                        Toast.makeText(this@TripDetailsActivity, "No internet and no cached countries available.", Toast.LENGTH_LONG).show()
                    } else if (!isOnline(applicationContext) && countryEntities.isNotEmpty()) {
                        Toast.makeText(this@TripDetailsActivity, "Offline mode: Displaying cached countries.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TripDetailsActivity, "Failed to load countries: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Uses the new CountryCurrencyEntity list
    private fun setupCountryDropdowns() {
        val countryNames = countryEntities.map { it.countryName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countryNames)

        fromInput.setAdapter(adapter)
        fromInput.threshold = 1
        toInput.setAdapter(adapter)
        toInput.threshold = 1
    }

    private fun saveTripToFirestore() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val from = fromInput.text.toString().trim()
        val to = toInput.text.toString().trim()
        val time = timeInput.text.toString().trim()
        val airport = airportInput.text.toString().trim()

        if (from.isEmpty() || to.isEmpty() || time.isEmpty() || airport.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val tripData = hashMapOf(
            "fromLocation" to from,
            "toLocation" to to,
            "time" to time,
            "airport" to airport,
            "createdAt" to System.currentTimeMillis(),
            "userId" to currentUser.uid
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("trips")
            .add(tripData)
            .addOnSuccessListener {
                Toast.makeText(this, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
                NotificationHelper.showNotification(
                    this,
                    "Trip Saved",
                    "Trip from $from to $to has been saved",
                    NotificationSettingsActivity.KEY_TRIP_NOTIFICATIONS
                )
                fromInput.text.clear()
                toInput.text.clear()
                timeInput.text.clear()
                airportInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving trip: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
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