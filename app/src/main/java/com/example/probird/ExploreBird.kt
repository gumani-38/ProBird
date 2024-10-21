package com.example.probird

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api.api
import com.example.probird.models.BirdTaxonomyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class ExploreBird : AppCompatActivity() {
    private lateinit var btnBackward: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var birdAdapter: BirdAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_explore_bird)

        // Setup insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        btnBackward = findViewById(R.id.btnBack)
        recyclerView = findViewById(R.id.recyclerView)

        // Set LayoutManager for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Back button listener
        btnBackward.setOnClickListener {
            val intent = Intent(this@ExploreBird, Home::class.java)
            startActivity(intent)
        }
        // Fetch bird data in the background
        fetchBirdData()
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun fetchBirdData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.eBirdTaxonomicRetrofit.getExploreBird("fcj0sukk3qm4", "json", "2019")
                if (response.isSuccessful) {
                    val birds: List<BirdTaxonomyItem> = response.body() ?: emptyList()
                      Log.v("countArr",birds.count().toString())
                    withContext(Dispatchers.Main) {
                        birdAdapter = BirdAdapter(birds)
                        recyclerView.adapter = birdAdapter
                        birdAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("ebird", "Error code: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ebird", "Exception occurred: ${e.message}")
            }
        }
    }
}