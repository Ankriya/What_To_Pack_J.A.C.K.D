package student.projects.prog7312_poe_jackd

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MenuActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        findViewById<Button>(R.id.SearchBtn).setOnClickListener {
            startActivity(Intent(this, MySearchActivity::class.java))
        }

        findViewById<Button>(R.id.SuitcaseBtn).setOnClickListener {
            startActivity(Intent(this, MySuitcaseActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_search -> {
                startActivity(Intent(this, MySearchActivity::class.java))
            }
            R.id.my_suitcase -> {
                startActivity(Intent(this, MySuitcaseActivity::class.java))
            }
            R.id.my_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
            }
            R.id.my_settings -> {
                startActivity(Intent(this, NotificationSettingsActivity::class.java))
            }
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