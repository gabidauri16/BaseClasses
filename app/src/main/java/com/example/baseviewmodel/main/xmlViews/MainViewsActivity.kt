package com.example.baseviewmodel.main.xmlViews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baseviewmodel.databinding.ActivityMainViewsBinding

class MainViewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainViewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}