package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // LOGIN HERE button triggers Google Sign-In
        findViewById<Button>(R.id.Loginbtn).setOnClickListener {
            signInWithGoogle()
        }

        // REGISTER button goes to RegisterActivity
        findViewById<Button>(R.id.RegBtn).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    // Save/update user to Firestore
                    user?.let {
                        val userData = hashMapOf(
                            "uid" to it.uid,
                            "email" to it.email,
                            "displayName" to it.displayName,
                            "photoUrl" to it.photoUrl?.toString(),
                            "lastLogin" to System.currentTimeMillis()
                        )

                        // Use merge to update existing fields or create new document
                        db.collection("users").document(it.uid)
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "User data saved/updated in Firestore")
                                Toast.makeText(this, "Welcome back ${user.displayName}!", Toast.LENGTH_SHORT).show()

                                // Navigate to main menu
                                startActivity(Intent(this, MenuActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error saving user data", e)
                                // Still navigate even if Firestore save fails
                                Toast.makeText(this, "Welcome back ${user.displayName}!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MenuActivity::class.java))
                                finish()
                            }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User already logged in, go to menu
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}