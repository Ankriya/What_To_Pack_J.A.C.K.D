package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var profileAvatar: ImageView
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var numberText: TextView
    private lateinit var genderText: TextView
    private lateinit var editProfileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        profileAvatar = findViewById(R.id.profile_avatar)
        fullNameText = findViewById(R.id.fullname)
        emailText = findViewById(R.id.email)
        numberText = findViewById(R.id.number)
        genderText = findViewById(R.id.gender)
        editProfileBtn = findViewById(R.id.EditProfilebtn)

        editProfileBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load profile photo from Firebase Auth
        currentUser.photoUrl?.let { photoUrl ->
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(profileAvatar)
        }

        // Load profile data from Firestore
        db.collection("users")
            .document(currentUser.uid)
            .collection("profile")
            .document("info")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("fullName") ?: currentUser.displayName ?: "Not set"
                    val email = document.getString("email") ?: currentUser.email ?: "Not set"
                    val number = document.getString("phoneNumber") ?: "Not set"
                    val gender = document.getString("gender") ?: "Not set"

                    fullNameText.text = "FULL NAME: $fullName"
                    emailText.text = "EMAIL: $email"
                    numberText.text = "NUMBER: $number"
                    genderText.text = "GENDER: $gender"
                } else {
                    // No profile data exists, show default from Firebase Auth
                    fullNameText.text = "FULL NAME: ${currentUser.displayName ?: "Not set"}"
                    emailText.text = "EMAIL: ${currentUser.email ?: "Not set"}"
                    numberText.text = "NUMBER: Not set"
                    genderText.text = "GENDER: Not set"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}