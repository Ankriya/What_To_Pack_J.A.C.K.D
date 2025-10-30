package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class EditProfileActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var profileIcon: ImageView
    private lateinit var fullNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var numberInput: EditText
    private lateinit var genderInput: EditText
    private lateinit var saveBtn: Button

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        profileIcon = findViewById(R.id.Icon)
        fullNameInput = findViewById(R.id.FullNameInput)
        emailInput = findViewById(R.id.EmailInput)
        numberInput = findViewById(R.id.NumberInput)
        genderInput = findViewById(R.id.GenderInput)
        saveBtn = findViewById(R.id.SaveBtn)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Hamburger menu button
        findViewById<ImageButton>(R.id.menu_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Load profile
        loadCurrentProfile()

        // Save profile button
        saveBtn.setOnClickListener {
            saveProfile()
        }

        // Handle window insets for notch/status bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadCurrentProfile() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load profile photo
        currentUser.photoUrl?.let { photoUrl ->
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(profileIcon)
        }

        // Load existing profile data
        db.collection("users")
            .document(currentUser.uid)
            .collection("profile")
            .document("info")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    fullNameInput.setText(document.getString("fullName") ?: "")
                    emailInput.setText(document.getString("email") ?: "")
                    numberInput.setText(document.getString("phoneNumber") ?: "")
                    genderInput.setText(document.getString("gender") ?: "")
                } else {
                    fullNameInput.setText(currentUser.displayName ?: "")
                    emailInput.setText(currentUser.email ?: "")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val fullName = fullNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phoneNumber = numberInput.text.toString().trim()
        val gender = genderInput.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }

        val profileData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "gender" to gender,
            "updatedAt" to System.currentTimeMillis(),
            "userId" to currentUser.uid
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("profile")
            .document("info")
            .set(profileData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Drawer navigation
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
