package com.example.egci428_travelguide.DataSource

import android.content.Context
import android.widget.ListView
import com.example.egci428_travelguide.Adapter.ProvincesAdapter
import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.DataModel.Region
import com.google.firebase.database.*

class RegionDataSource (regionInName: String, listView: ListView, context: Context){
    val firebaseURL = "https://egci428finalproject.firebaseio.com"
    val dataReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("province")
    lateinit var regionList:ArrayList<Region>
    val regionName = arrayOf("North","South","Central","West","East","Northeast")
    init{
        var tmp = ArrayList<Region>()
        regionName.forEach {
            tmp.add(Region(it,ArrayList<Province>()))
        }
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    val province = it.key
                    it.children.forEach{
                        if(it.key=="region") {
                            val index = regionName.indexOf(it.value.toString())
                            tmp[index].provinceList.add(Province(province!!,it.value.toString()))
                        }
                    }
                }
                println("Done add data")
                println("Function ended")
                regionList = tmp
                val regionReceived = getRegion(regionInName)
                println("Region in name "+regionInName)
                val arrayAdapter = ProvincesAdapter(context, regionReceived.provinceList)
                listView.setAdapter(arrayAdapter)
                println("Done set adapter")
            }
        })
    }

    fun getRegion(data_in : String): Region{
        var index = regionName.indexOf(data_in)
        //default case
        if(index==-1)index = regionName.indexOf("Central")
        println("index " +index)
        println("Region out  name "+regionList[index].name)
        return regionList.get(index)
    }
}