//
//package student.projects.prog7312_poe_jackd
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.navigation.NavigationView
//
//class MySearchActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    private lateinit var drawerLayout: DrawerLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_my_search)
//
//        // Setup drawer
//        drawerLayout = findViewById(R.id.drawer_layout)
//
//        // Setup navigation view
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener(this)
//
//        // Hamburger menu button
//        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }
//
//        // Events button
//        findViewById<Button>(R.id.EventsBtn).setOnClickListener {
//            startActivity(Intent(this, EventsActivity::class.java))
//        }
//
//        // Weather button
//        findViewById<Button>(R.id.WeatherBtn).setOnClickListener {
//            startActivity(Intent(this, WeatherActivity::class.java))
//        }
//
//        // Restaurant button
//        findViewById<Button>(R.id.RestaurantBtn).setOnClickListener {
//            startActivity(Intent(this, RestaurantActivity::class.java))
//        }
//
//        // Transport TextView (clickable)
//        findViewById<TextView>(R.id.TransportBtn).setOnClickListener {
//            startActivity(Intent(this, TransportActivity::class.java))
//        }
//
//        // Currency TextView (clickable)
//        findViewById<TextView>(R.id.CurrencyBtn).setOnClickListener {
//            startActivity(Intent(this, CurrencyActivity::class.java))
//        }
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.my_search -> {
//                // Already on this page
//            }
//            R.id.my_suitcase -> {
//                startActivity(Intent(this, MySuitcaseActivity::class.java))
//            }
//            R.id.my_profile -> {
//                startActivity(Intent(this, UserProfileActivity::class.java))
//            }
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }
//}
//

package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MySearchActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_search2)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // --- Hamburger menu button ---
        findViewById<ImageView>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Event button
        findViewById<ImageView>(R.id.EventsBtn).setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }

        // Weather button
        findViewById<ImageView>(R.id.WeatherBtn).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        // Restaurant button
        findViewById<ImageView>(R.id.RestaurantBtn).setOnClickListener {
            startActivity(Intent(this, RestaurantActivity::class.java))
        }

        // Transport button
        findViewById<ImageView>(R.id.TransportBtn).setOnClickListener {
            startActivity(Intent(this, TransportActivity::class.java))
        }

        // Currency button
        findViewById<ImageView>(R.id.CurrencyBtn).setOnClickListener {
            startActivity(Intent(this, CurrencyActivity::class.java))
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_search -> {

            }
            R.id.my_suitcase -> {
                startActivity(Intent(this, MySuitcaseActivity::class.java))
            }
            R.id.my_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
            }
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
