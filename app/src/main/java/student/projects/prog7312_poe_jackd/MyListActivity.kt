package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import android.widget.ImageButton

class MyListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListsAdapter
    private val itemsList = mutableListOf<ListItem>()

    private var listenerRegistration: ListenerRegistration? = null

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Hamburger menu button
        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserListsAdapter(itemsList) { selectedList ->
            val intent = Intent(this, ViewListDetailsActivity::class.java)
            intent.putExtra("LIST_ID", selectedList.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // New List button
        findViewById<Button>(R.id.new_list_button).setOnClickListener {
            startActivity(Intent(this, CreateListActivity::class.java))
        }

        // Load list items in real time
        startListeningForListItems()

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startListeningForListItems() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        // Real-time listener
        listenerRegistration = db.collection("users")
            .document(currentUser.uid)
            .collection("lists")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    itemsList.clear()
                    for (doc in snapshots) {
                        val item = ListItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0L,
                            userId = doc.getString("userId") ?: ""
                        )
                        itemsList.add(item)
                    }
                    adapter.notifyDataSetChanged()

                    if (itemsList.isEmpty()) {
                        Toast.makeText(this, "No items in your list yet!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    // Drawer navigation
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
