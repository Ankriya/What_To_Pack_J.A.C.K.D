package student.projects.prog7312_poe_jackd

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewTripsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripsAdapter
    private val tripsList = mutableListOf<Trip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trips)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.trips_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TripsAdapter(tripsList)
        recyclerView.adapter = adapter

        loadTrips()

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
}