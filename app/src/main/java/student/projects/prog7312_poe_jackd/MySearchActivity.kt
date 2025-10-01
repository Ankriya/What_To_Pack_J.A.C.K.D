package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MySearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_search)

        // Events button
        findViewById<Button>(R.id.EventsBtn).setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }

        // Weather button
        findViewById<Button>(R.id.WeatherBtn).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        // Restaurant button
        findViewById<Button>(R.id.RestaurantBtn).setOnClickListener {
            startActivity(Intent(this, RestaurantActivity::class.java))
        }

        // Transport TextView (clickable)
        findViewById<TextView>(R.id.TransportBtn).setOnClickListener {
            startActivity(Intent(this, TransportActivity::class.java))
        }

        // Currency TextView (clickable)
        findViewById<TextView>(R.id.CurrencyBtn).setOnClickListener {
            startActivity(Intent(this, CurrencyActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}