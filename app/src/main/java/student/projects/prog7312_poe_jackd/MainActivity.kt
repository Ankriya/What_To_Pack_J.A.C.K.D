package student.projects.prog7312_poe_jackd

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfg.navigationdrawerkotlin.NavDrawer
import kotlin.math.abs // Ensure this is imported
import student.projects.prog7312_poe_jackd.LocaleHelper
import student.projects.prog7312_poe_jackd.PreferenceManager

class MainActivity : BaseActivity() {

    private val TAG = "MainActivitySwipe"

    private var x1 = 0f // Initial x-coordinate of the touch event
    private var x2 = 0f // Final x-coordinate of the touch event

    // Minimum distance in pixels for detecting a horizontal swipe
    private val MIN_DISTANCE = 150

    override fun attachBaseContext(newBase: android.content.Context) {
        val lang = PreferenceManager.getLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val btnAfrikaans = findViewById<Button>(R.id.btnAfrikaans)
        val btnZulu = findViewById<Button>(R.id.btnZulu)

        btnEnglish.setOnClickListener { changeLanguage("en") }
        btnAfrikaans.setOnClickListener { changeLanguage("af") }
        btnZulu.setOnClickListener { changeLanguage("zu") }

        // It is CRITICAL that the main container is set to be clickable/touchable
        // so that it receives the MotionEvent.ACTION_DOWN first.
        mainLayout.isClickable = true
        mainLayout.isFocusable = true


        // Set up a touch listener on the main layout to detect swipe actions
        mainLayout.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x // Store the initial x-coordinate of the touch
                    // We must return true here to ensure ACTION_UP is received
                    true
                }
                MotionEvent.ACTION_UP -> {
                    x2 = event.x // Store the x-coordinate when the touch is released
                    val deltaX = x2 - x1 // Calculate the horizontal distance

                    // Check if the absolute swipe distance is greater than the minimum threshold
                    if (abs(deltaX) > MIN_DISTANCE) {

                        // **FIXED LOGIC**: Check for RIGHT SWIPE (x2 > x1, so deltaX is positive)
                        if (deltaX > 0) {
                            Log.d(TAG, "onTouch: Right swipe detected. Navigating to next page.")

                            // 1. Target the NEXT activity (e.g., NavDrawer or LoginActivity)
                            // Using LoginActivity as per your code:
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                            // 2. Apply sliding transition (Recommended for a right swipe)
                            // Current page slides right (out), new page slides in from the left.
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

                        } else {
                            Log.d(TAG, "onTouch: Left swipe detected (not handled for navigation).")
                        }
                    }
                    true // Consume the event
                }
                else -> false // Ignore other motion events
            }
        }

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun changeLanguage(languageCode: String) {
        PreferenceManager.saveLanguage(this, languageCode)
        LocaleHelper.setLocale(this, languageCode)
        recreate() // Refresh UI text instantly
    }
}