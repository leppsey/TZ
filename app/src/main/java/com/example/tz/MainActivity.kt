package com.example.tz


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityVM::class.java)
        val enterBinTextView: AutoCompleteTextView = findViewById(R.id.enterBIN)
        viewModel.BINHistory.observe(this, Observer {
            enterBinTextView.setAdapter(ArrayAdapter(this, android.R.layout.select_dialog_item, it))
        })
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

    }
}