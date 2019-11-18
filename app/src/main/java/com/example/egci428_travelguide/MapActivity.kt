package com.example.egci428_travelguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        addClickEventListener()
    }
    private fun addClickEventListener(){
        // get reference to Text
        val northRegion = findViewById(R.id.northText) as TextView
        val centralRegion = findViewById(R.id.centralText) as TextView
        val northeastRegion = findViewById(R.id.northeastText) as TextView
        val westRegion = findViewById(R.id.westText) as TextView
        val eastRegion = findViewById(R.id.eastText) as TextView
        val southRegion = findViewById(R.id.southText) as TextView
        // set on-click listener
        northRegion.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@MapActivity, "North is clicked", Toast.LENGTH_SHORT).show()
        }
        centralRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "Central is clicked", Toast.LENGTH_SHORT).show()
        }
        northeastRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "Northeast is clicked", Toast.LENGTH_SHORT).show()
        }
        westRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "West is clicked", Toast.LENGTH_SHORT).show()
        }
        eastRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "East is clicked", Toast.LENGTH_SHORT).show()
        }
        southRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "South is clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
