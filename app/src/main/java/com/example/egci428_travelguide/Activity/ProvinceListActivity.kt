package com.example.egci428_travelguide.Activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.egci428_travelguide.*
import com.example.egci428_travelguide.Adapter.ProvincesAdapter
import com.example.egci428_travelguide.DataModel.Region
import com.example.egci428_travelguide.DataSource.RegionDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_province_list.*

class ProvinceListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit private var dataSource: RegionDataSource
    var region = "Central" //default case
    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_list)
        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        val data = intent.extras
        if(data!=null){
            region = data.getString("region")!!
            println(region)
            //get pressed region and fetch data - show list in RegionDataSource
            dataSource = RegionDataSource(region, provinceList!!, this)
        }
        regionName.setText(region)
    }
    override fun onResume() {
        // once user leaves the page and comeback, the app will require fingerprint authentication
        super.onResume()
        if(i!=0){
            val keyguardmng= getSystemService(Context.KEYGUARD_SERVICE)
                    as KeyguardManager
            val fingerprintmng = getSystemService(Context.FINGERPRINT_SERVICE)
                    as FingerprintManager
            val fingerprintauth = FingerprintAuth(this, keyguardmng, fingerprintmng)
            if (fingerprintauth.checkLockScreen()) {
                fingerprintauth.generateKey()
                if (fingerprintauth.initCipher()) {
                    fingerprintauth.cipher.let {
                        fingerprintauth.cryptoObject = FingerprintManager.CryptoObject(it)
                    }
                    val helper = FingerprintHelper(this)
                    if (fingerprintauth.fingerprintManager != null && fingerprintauth.cryptoObject != null) {
                        helper.startAuth(fingerprintauth.fingerprintManager, fingerprintauth.cryptoObject)
                    }
                }
            }
        }
        i++
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_detail,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.profileItem){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("parent","List")
            intent.putExtra("region", region)
            startActivity(intent)
            finish()
        }else if(id == R.id.signoutItem){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == android.R.id.home){
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}