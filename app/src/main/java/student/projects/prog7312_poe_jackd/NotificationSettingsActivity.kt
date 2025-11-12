package student.projects.prog7312_poe_jackd

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class NotificationSettingsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var switchListNotifications: SwitchMaterial
    private lateinit var switchTripNotifications: SwitchMaterial
    private lateinit var switchProfileNotifications: SwitchMaterial
    private lateinit var switchWeatherNotifications: SwitchMaterial
    private lateinit var switchRestaurantNotifications: SwitchMaterial
    private lateinit var switchEventNotifications: SwitchMaterial
    private lateinit var switchTransportNotifications: SwitchMaterial
    private lateinit var switchCurrencyNotifications: SwitchMaterial

    companion object {
        const val PREFS_NAME = "NotificationPrefs"
        const val CHANNEL_ID = "app_notifications"
        const val KEY_LIST_NOTIFICATIONS = "list_notifications"
        const val KEY_TRIP_NOTIFICATIONS = "trip_notifications"
        const val KEY_PROFILE_NOTIFICATIONS = "profile_notifications"
        const val KEY_WEATHER_NOTIFICATIONS = "weather_notifications"
        const val KEY_RESTAURANT_NOTIFICATIONS = "restaurant_notifications"
        const val KEY_EVENT_NOTIFICATIONS = "event_notifications"
        const val KEY_TRANSPORT_NOTIFICATIONS = "transport_notifications"
        const val KEY_CURRENCY_NOTIFICATIONS = "currency_notifications"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        createNotificationChannel()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        switchListNotifications = findViewById(R.id.switch_list_notifications)
        switchTripNotifications = findViewById(R.id.switch_trip_notifications)
        switchProfileNotifications = findViewById(R.id.switch_profile_notifications)
        switchWeatherNotifications = findViewById(R.id.switch_weather_notifications)
        switchRestaurantNotifications = findViewById(R.id.switch_restaurant_notifications)
        switchEventNotifications = findViewById(R.id.switch_event_notifications)
        switchTransportNotifications = findViewById(R.id.switch_transport_notifications)
        switchCurrencyNotifications = findViewById(R.id.switch_currency_notifications)

        loadPreferences()

        switchListNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_LIST_NOTIFICATIONS, isChecked)
        }

        switchTripNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_TRIP_NOTIFICATIONS, isChecked)
        }

        switchProfileNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_PROFILE_NOTIFICATIONS, isChecked)
        }

        switchWeatherNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_WEATHER_NOTIFICATIONS, isChecked)
        }

        switchRestaurantNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_RESTAURANT_NOTIFICATIONS, isChecked)
        }

        switchEventNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_EVENT_NOTIFICATIONS, isChecked)
        }

        switchTransportNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_TRANSPORT_NOTIFICATIONS, isChecked)
        }

        switchCurrencyNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference(KEY_CURRENCY_NOTIFICATIONS, isChecked)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Notifications"
            val descriptionText = "Notifications for app activities"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        switchListNotifications.isChecked = prefs.getBoolean(KEY_LIST_NOTIFICATIONS, true)
        switchTripNotifications.isChecked = prefs.getBoolean(KEY_TRIP_NOTIFICATIONS, true)
        switchProfileNotifications.isChecked = prefs.getBoolean(KEY_PROFILE_NOTIFICATIONS, true)
        switchWeatherNotifications.isChecked = prefs.getBoolean(KEY_WEATHER_NOTIFICATIONS, true)
        switchRestaurantNotifications.isChecked = prefs.getBoolean(KEY_RESTAURANT_NOTIFICATIONS, true)
        switchEventNotifications.isChecked = prefs.getBoolean(KEY_EVENT_NOTIFICATIONS, true)
        switchTransportNotifications.isChecked = prefs.getBoolean(KEY_TRANSPORT_NOTIFICATIONS, true)
        switchCurrencyNotifications.isChecked = prefs.getBoolean(KEY_CURRENCY_NOTIFICATIONS, true)
    }

    private fun savePreference(key: String, value: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
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