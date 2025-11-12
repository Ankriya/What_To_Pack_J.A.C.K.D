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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fromInput: AutoCompleteTextView
    private lateinit var toInput: AutoCompleteTextView
    private lateinit var amountInput: EditText
    private lateinit var resultDisplay: TextView

    // Repository instance to access data (API or Room)
    private lateinit var currencyRepository: CurrencyRepository

    // List now holds the cached entity objects
    private var countryEntities = listOf<CountryCurrencyEntity>()

    // Drawer variables
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageButton

    // Helper function for connectivity check (moved here for simplicity without a full ViewModel)
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_currency)

        fromInput = findViewById(R.id.FromInput)
        toInput = findViewById(R.id.ToInput)
        amountInput = findViewById(R.id.AmountInput)
        resultDisplay = findViewById(R.id.ResultDisplay)

        // Repository Initialization
        val apiService = RetrofitClient.instance
        val currencyDao = CurrencyDatabase.getDatabase(applicationContext).countryCurrencyDao()
        currencyRepository = CurrencyRepository(apiService, currencyDao, applicationContext)

        // Load countries using the Repository logic
        loadCountries()

        findViewById<Button>(R.id.SubmitBtn).setOnClickListener {
            convertCurrency()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.menu_button)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)
    }

    // Uses Coroutines and the Repository for offline-first data fetching
    private fun loadCountries() {
        // Launch a coroutine on the IO dispatcher for network/DB operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // The Repository handles the logic: try API, fall back to Room
                val entities = currencyRepository.getCountryCurrencies()

                // Switch back to the Main thread to update the UI
                withContext(Dispatchers.Main) {
                    countryEntities = entities
                    setupCurrencyDropdowns()

                    // User Feedback based on state
                    if (entities.isEmpty()) {
                        Toast.makeText(this@CurrencyActivity, "No internet and no cached currencies available.", Toast.LENGTH_LONG).show()
                    } else if (!isOnline(applicationContext) && countryEntities.isNotEmpty()) {
                        Toast.makeText(this@CurrencyActivity, "Offline mode: Displaying cached currencies.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle critical errors (e.g., first run and no network)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CurrencyActivity, "Failed to load currencies: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Uses the CountryCurrencyEntity list
    private fun setupCurrencyDropdowns() {
        val currencies = countryEntities.map { "${it.currencyCode} (${it.countryName})" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, currencies)

        fromInput.setAdapter(adapter)
        fromInput.threshold = 1

        toInput.setAdapter(adapter)
        toInput.threshold = 1
    }

    private fun convertCurrency() {
        val fromCurrency = fromInput.text.toString().trim().split(" ")[0]
        val toCurrency = toInput.text.toString().trim().split(" ")[0]
        val amount = amountInput.text.toString().trim()

        if (fromCurrency.isEmpty() || toCurrency.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.convertCurrency(fromCurrency, toCurrency, amountDouble)
            .enqueue(object : Callback<CurrencyResponse> {
                override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            resultDisplay.text = "Converted Result: ${it.result} ${it.to}"
                        }
                    } else {
                        Toast.makeText(this@CurrencyActivity, "Conversion failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    Toast.makeText(this@CurrencyActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Navigation Drawer Methods
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