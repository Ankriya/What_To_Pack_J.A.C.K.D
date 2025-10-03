package student.projects.prog7312_poe_jackd

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ViewListDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListItemsAdapter
    private val itemsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_list_details)

        val listId = intent.getStringExtra("LIST_ID") ?: return
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.list_items_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListItemsAdapter(itemsList)
        recyclerView.adapter = adapter

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
}
