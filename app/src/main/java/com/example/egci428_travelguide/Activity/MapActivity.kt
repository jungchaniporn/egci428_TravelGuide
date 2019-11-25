package com.example.egci428_travelguide.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.egci428_travelguide.Activity.ProvinceListActivity
import com.example.egci428_travelguide.DataSource.RegionDataSource
import com.example.egci428_travelguide.ProfileActivity
import com.example.egci428_travelguide.R
import com.example.egci428_travelguide.SigninActivity
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        addClickEventListener()

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
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
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "North");
            startActivity(intent)
            //finish()
        }
        centralRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "Central is clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "Central");
            startActivity(intent)
            //finish()
        }
        northeastRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "Northeast is clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "Northeast");
            startActivity(intent)
            //finish()
        }
        westRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "West is clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "West");
            startActivity(intent)
            //finish()
        }
        eastRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "East is clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "East");
            startActivity(intent)
            //finish()
        }
        southRegion.setOnClickListener {
            Toast.makeText(this@MapActivity, "South is clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MapActivity, ProvinceListActivity::class.java)
            intent.putExtra("region", "South");
            startActivity(intent)
            //finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_detail,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.profileItem){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }else if(id == R.id.signoutItem){
            auth.signOut()
            finish()
        }else if(id == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

//    override fun onResume() {
//        super.onResume()
//        val intent = Intent(this@MapActivity, SigninActivity::class.java)
//        startActivity(intent)
//    }
}
