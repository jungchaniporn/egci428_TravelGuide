package com.example.egci428_travelguide.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.egci428_travelguide.Adapter.ProvincesAdapter
import com.example.egci428_travelguide.DataModel.Region
import com.example.egci428_travelguide.DataSource.RegionDataSource
import com.example.egci428_travelguide.R
import kotlinx.android.synthetic.main.activity_province_list.*

class ProvinceListActivity : AppCompatActivity() {
    lateinit private var dataSource: RegionDataSource
    var region = "Central" //default case
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_list)
        val data = intent.extras
        if(data!=null){
            region = data.getString("region")!!
            println(region)
            //get pressed region and fetch data - show list in RegionDataSource
            dataSource = RegionDataSource(region, provinceList, this)
        }
        regionName.setText(region)
    }
}