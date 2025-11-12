package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewListDetailsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListItemsAdapter
    private val itemsList = mutableListOf<String>()

    private lateinit var btnEditList: ImageButton
    private lateinit var btnDeleteList: ImageButton
    private lateinit var addItemBtn: ImageButton
    private lateinit var newItemInput: TextInputEditText
    private lateinit var drawerLayout: DrawerLayout
    private var isEditMode = false
    private lateinit var listId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_list_details)

        listId = intent.getStringExtra("LIST_ID") ?: return
        db = FirebaseFirestore.getInstance()

        // 🔹 Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // 🔹 Hamburger menu button
        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        recyclerView = findViewById(R.id.list_items_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListItemsAdapter(itemsList, listId, this)
        recyclerView.adapter = adapter

        btnEditList = findViewById(R.id.btn_edit_list)
        btnDeleteList = findViewById(R.id.btn_delete_list)
        addItemBtn = findViewById(R.id.add_item_btn)
        newItemInput = findViewById(R.id.new_item_input)

        // 🔹 Toggle edit mode
        btnEditList.setOnClickListener {
            isEditMode = !isEditMode
            adapter.setEditMode(isEditMode)
        }

        // 🔹 Delete entire list
        btnDeleteList.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Delete List")
                .setMessage("Are you sure you want to delete this entire list?")
                .setPositiveButton("Yes") { _, _ ->
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("lists")
                        .document(listId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "List deleted successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete list: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // 🔹 Add new item
        addItemBtn.setOnClickListener {
            val itemName = newItemInput.text.toString().trim()
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Enter an item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            val listRef = db.collection("users")
                .document(currentUser.uid)
                .collection("lists")
                .document(listId)

            listRef.update("items", com.google.firebase.firestore.FieldValue.arrayUnion(itemName))
                .addOnSuccessListener {
                    itemsList.add(itemName)
                    adapter.notifyItemInserted(itemsList.size - 1)
                    newItemInput.text?.clear()
                    recyclerView.scrollToPosition(itemsList.size - 1)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add item: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        findViewById<Button>(R.id.EditProfilebtn).setOnClickListener { finish() }

        loadListDetails(listId)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadListDetails(listId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        db.collection("users")
            .document(currentUser.uid)
            .collection("lists")
            .document(listId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val listTitle = doc.getString("title") ?: ""
                    val listItems = doc.get("items") as? List<String> ?: emptyList()

                    findViewById<TextView>(R.id.list_name_header).text = listTitle

                    itemsList.clear()
                    itemsList.addAll(listItems)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load list: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔹 Handle navigation drawer item clicks
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
