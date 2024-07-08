package com.cg.chatapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cg.chatapp.R
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.main_nav_host_fragment)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.employeesFragment,
                R.id.chatsFragment,
                R.id.adminFragment
            )
        )

        if (prefs.getUserAccess() == ADMIN) {
            bottomNavigationView.menu.findItem(R.id.adminFragment).isVisible = true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.employeesFragment, R.id.chatsFragment, R.id.adminFragment -> {
                binding.bottomNavigationView.visibility = View.VISIBLE
                title = getString(R.string.app_name)
            }
            else ->
                binding.bottomNavigationView.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration)

    companion object {
        private const val ADMIN = "admin"
    }
}