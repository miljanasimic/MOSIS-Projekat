package elfak.mosis.petfinder

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import elfak.mosis.petfinder.databinding.ActivityMainBinding
import elfak.mosis.petfinder.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val bottomNav=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        //val tabBar=findViewById<TabLayout>(R.id.tabLayoutFriends)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.HomeFragment, R.id.MapFragment,  R.id.NewPostFragment, R.id.RankFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    //tabBar.visibility = View.GONE
                }
                R.id.FriendsFragment -> {
                    bottomNav.visibility = View.VISIBLE
                    //tabBar.visibility = View.VISIBLE
                }
                else -> {
                    bottomNav.visibility = View.GONE
                    //tabBar.visibility = View.GONE
                }
            }
        }
        //TODO backstack za fragmente
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        Toast.makeText(applicationContext,"back",Toast.LENGTH_LONG).show()
    }

}