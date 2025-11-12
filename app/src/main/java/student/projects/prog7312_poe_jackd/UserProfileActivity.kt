package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton // Import ImageButton for the menu button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat // For opening/closing the drawer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout // Import DrawerLayout
import com.google.android.material.navigation.NavigationView // Import NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

// Implement NavigationView.OnNavigationItemSelectedListener
class UserProfileActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Navigation Drawer components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageButton // The custom hamburger icon

    private lateinit var profileAvatar: ImageView
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var numberText: TextView
    private lateinit var genderText: TextView
    private lateinit var editProfileBtn: Button

    private lateinit var btnEnglish: Button
    private lateinit var btnAfrikaans: Button
    private lateinit var btnZulu: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        btnEnglish = findViewById(R.id.btnEnglish)
        btnAfrikaans = findViewById(R.id.btnAfrikaans)
        btnZulu = findViewById(R.id.btnZulu)

        btnEnglish.setOnClickListener { changeLanguage("en") }
        btnAfrikaans.setOnClickListener { changeLanguage("af") }
        btnZulu.setOnClickListener { changeLanguage("zu") }


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Nav Drawer components
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.menu_button)

        // Set up Nav Drawer listener
        navView.setNavigationItemSelectedListener(this)

        // Set click listener for the custom ImageButton to open the drawer
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        profileAvatar = findViewById(R.id.profile_avatar)
        fullNameText = findViewById(R.id.fullname)
        emailText = findViewById(R.id.email)
        numberText = findViewById(R.id.number)
        genderText = findViewById(R.id.gender)
        editProfileBtn = findViewById(R.id.EditProfilebtn)

        editProfileBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Apply Window Insets (kept for compatibility with your existing code)
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

    // New: Handle back press to close the drawer first
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // New: Handle clicks on navigation drawer items
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Use an Intent to navigate to the corresponding Activity
        when (item.itemId) {
            R.id.my_search -> {
                startActivity(Intent(this, MySearchActivity::class.java))
            }

            R.id.my_suitcase -> {
                startActivity(Intent(this, MySuitcaseActivity::class.java))
            }

            R.id.my_profile -> {
                // Already on UserProfileActivity, maybe just close drawer
                Toast.makeText(this, "You are already here!", Toast.LENGTH_SHORT).show()
            }

            R.id.my_settings -> {
                startActivity(Intent(this, NotificationSettingsActivity::class.java))
            }
        }

        // Close the drawer after navigation
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadUserProfile() {
        // ... (Your existing loadUserProfile function remains the same)
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

                    fullNameText.text = "${getString(R.string.FullName)} $fullName"
                    emailText.text = "${getString(R.string.Email)} $email"
                    numberText.text = "${getString(R.string.Number)} $number"
                    genderText.text = "${getString(R.string.Gender)} $gender"
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
    private fun changeLanguage(languageCode: String) {
        PreferenceManager.saveLanguage(this, languageCode)
        LocaleHelper.setLocale(this, languageCode)
        recreate() // refresh the activity UI immediately
    }

}