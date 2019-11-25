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
import com.example.egci428_travelguide.DataSource.ProvincePlacesDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_province_list.*
import kotlinx.android.synthetic.main.activity_province_places.*

class ProvincePlacesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit private var dataSource: ProvincePlacesDataSource
    var region = ""
    var province = ""
    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_places)
        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val data = intent.extras
        if(data!=null){
            province = data.getString("province")!!
            region = data.getString("region")!!
            println(province)
            dataSource = ProvincePlacesDataSource(province, region, placeList, this)
        }
        provinceName.setText(province)

        addPlaceBtn.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            intent.putExtra("province",province)
            intent.putExtra("region",region)
            intent.putExtra("from","provincePlace")
            startActivity(intent)
            finish()
        }
    }
    override fun onResume() {
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
            startActivity(intent)
        }else if(id == R.id.signoutItem){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == android.R.id.home){
            val intent = Intent(this, ProvinceListActivity::class.java)
            intent.putExtra("province",province)
            intent.putExtra("region",region)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
