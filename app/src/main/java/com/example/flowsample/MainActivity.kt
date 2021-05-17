package com.example.flowsample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flowsample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private lateinit var networkListener: NetworkListener
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        networkListener = NetworkListener(this)

        // This coroutine is tied to the lifecycle of this activity
        // If this activity is destroyed, the scope is automatically cancelled
        lifecycleScope.launch {
            val networkFlow = networkListener.subscribeToNetworkFlow()

            networkFlow.collect { number ->
                binding.message.text = "Your network was disconnected $number times"
            }
        }
    }
}