package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SimpleListItemsAdapter
    private val itemsList = mutableListOf<String>()

    private lateinit var newItemInput: EditText
    private lateinit var addItemBtn: Button
    private lateinit var saveListBtn: Button
    private lateinit var titleInput: EditText

    // Drawer variables
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_list)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        titleInput = findViewById(R.id.ListTitleInput)
        newItemInput = findViewById(R.id.NewItemInput)
        addItemBtn = findViewById(R.id.AddItemBtn)
        saveListBtn = findViewById(R.id.SaveList)
        recyclerView = findViewById(R.id.items_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SimpleListItemsAdapter(itemsList)
        recyclerView.adapter = adapter

        addItemBtn.setOnClickListener { addItemToList() }
        saveListBtn.setOnClickListener { saveListToFirestore() }

        findViewById<Button>(R.id.ViewList).setOnClickListener {
            startActivity(Intent(this, MyListActivity::class.java))
        }

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Drawer setup ---
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.menu_button)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)
    }

    private fun addItemToList() {
        val itemName = newItemInput.text.toString().trim()
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show()
            return
        }

        itemsList.add(itemName)
        adapter.notifyItemInserted(itemsList.size - 1)
        newItemInput.text.clear()
    }

    private fun saveListToFirestore() {
        val currentUser = auth.currentUser ?: run {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        if (itemsList.isEmpty()) {
            Toast.makeText(this, "Add at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        val title = titleInput.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a list title", Toast.LENGTH_SHORT).show()
            return
        }

        val newList = ListItem(
            title = title,
            items = itemsList.toList(),
            createdAt = System.currentTimeMillis(),
            userId = currentUser.uid
        )

        val userListsRef = db.collection("users")
            .document(currentUser.uid)
            .collection("lists")
            .document()

        userListsRef.set(newList)
            .addOnSuccessListener {
                Toast.makeText(this, "List saved successfully!", Toast.LENGTH_SHORT).show()
                itemsList.clear()
                adapter.notifyDataSetChanged()
                titleInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving list: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Navigation Drawer Methods ---
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
