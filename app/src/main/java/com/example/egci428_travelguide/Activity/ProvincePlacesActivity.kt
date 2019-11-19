package com.example.egci428_travelguide.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.egci428_travelguide.DataSource.ProvincePlacesDataSource
import com.example.egci428_travelguide.R
import kotlinx.android.synthetic.main.activity_province_list.*
import kotlinx.android.synthetic.main.activity_province_places.*

class ProvincePlacesActivity : AppCompatActivity() {
    lateinit private var dataSource: ProvincePlacesDataSource
    var province = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_places)
        val data = intent.extras
        if(data!=null){
            province = data.getString("province")!!
            println(province)
            dataSource = ProvincePlacesDataSource(province, placeList, this)
        }
        provinceName.setText(province)
    }
}
