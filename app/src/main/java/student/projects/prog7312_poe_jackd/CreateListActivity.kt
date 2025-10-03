package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListItemsAdapter
    private val itemsList = mutableListOf<ListItem>()

    private lateinit var newItemInput: EditText
    private lateinit var addItemBtn: Button
    private lateinit var saveListBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)

        setContentView(R.layout.activity_create_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        newItemInput = findViewById(R.id.NewItemInput)
        addItemBtn = findViewById(R.id.AddItemBtn)
        saveListBtn = findViewById(R.id.SaveList)
        recyclerView = findViewById(R.id.items_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListItemsAdapter(itemsList)
        recyclerView.adapter = adapter

        addItemBtn.setOnClickListener { addItemToList() }
        saveListBtn.setOnClickListener { saveListToFirestore() }


        //New List button
        findViewById<Button>(R.id.ViewList).setOnClickListener {
            startActivity(Intent(this, MyListActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addItemToList() {
        val itemName = newItemInput.text.toString().trim()
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show()
            return
        }

        itemsList.add(ListItem(name = itemName))
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

        val batch = db.batch()
        val userListsRef = db.collection("users").document(currentUser.uid).collection("lists")
        for (item in itemsList) {
            val docRef = userListsRef.document()
            batch.set(docRef, hashMapOf(
                "name" to item.name,
                "createdAt" to System.currentTimeMillis(),
                "userId" to currentUser.uid
            ))
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "List saved successfully!", Toast.LENGTH_SHORT).show()
                itemsList.clear()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving list: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}