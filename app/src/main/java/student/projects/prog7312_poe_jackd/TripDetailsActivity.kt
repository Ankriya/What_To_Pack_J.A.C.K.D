package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var fromInput: EditText
    private lateinit var toInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var airportInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fromInput = findViewById(R.id.FromInput)
        toInput = findViewById(R.id.ToInput)
        timeInput = findViewById(R.id.TimeInput)
        airportInput = findViewById(R.id.AirportInput)

        findViewById<Button>(R.id.SubmitBtn).setOnClickListener {
            saveTripToFirestore()
        }

        findViewById<Button>(R.id.ViewTripsBtn).setOnClickListener {
            startActivity(Intent(this, ViewTripsActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveTripToFirestore() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val from = fromInput.text.toString().trim()
        val to = toInput.text.toString().trim()
        val time = timeInput.text.toString().trim()
        val airport = airportInput.text.toString().trim()

        if (from.isEmpty() || to.isEmpty() || time.isEmpty() || airport.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val tripData = hashMapOf(
            "fromLocation" to from,
            "toLocation" to to,
            "time" to time,
            "airport" to airport,
            "createdAt" to System.currentTimeMillis(),
            "userId" to currentUser.uid
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("trips")
            .add(tripData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Trip saved successfully!", Toast.LENGTH_SHORT).show()

                fromInput.text.clear()
                toInput.text.clear()
                timeInput.text.clear()
                airportInput.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving trip: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}