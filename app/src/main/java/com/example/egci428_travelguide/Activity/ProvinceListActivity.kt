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
    lateinit var regionData:Region
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_list)
        val data = intent.extras
        if(data!=null){
            region = data.getString("region")!!
            println(region)
            dataSource = RegionDataSource(region, provinceList, this)
        }
        regionName.setText(region)
//        val regionlist = dataSource.regionList
//        println("Region List: "+regionlist.size+" "+regionlist.get(0).provinceList.size)
//        regionData = dataSource.getRegion(region)
//        println("Region Data: "+regionData.name+" "+regionData.provinceList.size)
//        val arrayAdapter = ProvincesAdapter(this, regionData.provinceList)
//        provinceList.setAdapter(arrayAdapter)
    }
}