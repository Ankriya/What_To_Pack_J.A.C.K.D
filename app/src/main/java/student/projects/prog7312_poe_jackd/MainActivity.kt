package student.projects.prog7312_poe_jackd

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
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
import kotlin.math.abs
import student.projects.prog7312_poe_jackd.LocaleHelper
import student.projects.prog7312_poe_jackd.PreferenceManager

class MainActivity : BaseActivity() {

    private val TAG = "MainActivitySwipe"

    private var x1 = 0f
    private var x2 = 0f

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

        createNotificationChannel()

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val btnAfrikaans = findViewById<Button>(R.id.btnAfrikaans)
        val btnZulu = findViewById<Button>(R.id.btnZulu)

        btnEnglish.setOnClickListener { changeLanguage("en") }
        btnAfrikaans.setOnClickListener { changeLanguage("af") }
        btnZulu.setOnClickListener { changeLanguage("zu") }

        mainLayout.isClickable = true
        mainLayout.isFocusable = true

        mainLayout.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    x2 = event.x
                    val deltaX = x2 - x1

                    if (abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX > 0) {
                            Log.d(TAG, "onTouch: Right swipe detected. Navigating to next page.")

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

                        } else {
                            Log.d(TAG, "onTouch: Left swipe detected (not handled for navigation).")
                        }
                    }
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Notifications"
            val descriptionText = "Notifications for app activities"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NotificationSettingsActivity.CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun changeLanguage(languageCode: String) {
        PreferenceManager.saveLanguage(this, languageCode)
        LocaleHelper.setLocale(this, languageCode)
        recreate()
    }
}