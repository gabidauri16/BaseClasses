package com.example.baseviewmodel.main.xmlViews

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.baseviewmodel.R
import com.example.baseviewmodel.databinding.ActivityMainViewsBinding

class MainViewsActivity : AppCompatActivity() {

//    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_views)
                binding = ActivityMainViewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navController = findNavController(R.id.nav_host_fragment_content_main_views)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main_views)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}