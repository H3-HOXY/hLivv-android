package com.hoxy.hlivv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoxy.hlivv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navView: CustomBottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        navView.setupWithNavController(navController)
//
//        val fab: FloatingActionButton = binding.homeButton
//        fab.setOnClickListener {
//            navController.navigate(R.id.navigation_home)
//        }

    }
}