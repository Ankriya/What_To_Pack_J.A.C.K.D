package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewTripsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripsAdapter
    private val tripsList = mutableListOf<Trip>()

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trips)

        // 🔹 Setup navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // 🔹 Setup hamburger (menu) button
        findViewById<ImageButton>(R.id.menu_button)?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 🔹 Firebase setup
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 🔹 RecyclerView setup
        recyclerView = findViewById(R.id.trips_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TripsAdapter(tripsList)
        recyclerView.adapter = adapter

        // 🔹 Load trip data
        loadTrips()

        // 🔹 Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadTrips() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .document(currentUser.uid)
            .collection("trips")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                tripsList.clear()

                for (document in documents) {
                    val trip = Trip(
                        id = document.id,
                        fromLocation = document.getString("fromLocation") ?: "",
                        toLocation = document.getString("toLocation") ?: "",
                        time = document.getString("time") ?: "",
                        airport = document.getString("airport") ?: "",
                        createdAt = document.getLong("createdAt") ?: 0L,
                        userId = document.getString("userId") ?: ""
                    )
                    tripsList.add(trip)
                }

                adapter.notifyDataSetChanged()

                if (tripsList.isEmpty()) {
                    Toast.makeText(this, "No trips saved yet!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔹 Navigation drawer item handling
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

    // 🔹 Back button closes drawer if open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
