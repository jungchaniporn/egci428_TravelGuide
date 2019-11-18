package com.example.egci428_travelguide.DataSource

import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.DataModel.Region
import com.google.firebase.database.*

class RegionDataSource {
    val firebaseURL = "https://egci428finalproject.firebaseio.com"
    val dataReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("province")
    lateinit var regionList:ArrayList<Region>
    val regionName = arrayOf<String>("North","South","Central","West","East","Northeast")
    init{
        regionList = ArrayList<Region>()
        regionName.forEach {
            regionList.add(Region(it,ArrayList<Province>()))
        }
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    val province = it.key
                    var region = ""
                    it.children.forEach{
                        if(it.key=="region")region = it.value.toString()
                    }
                    val index = regionName.indexOf(region)
                    regionList[index].provinceList.add(Province(province!!,region))
                }
                regionList.forEach{
                    println(it.name)
                    println(it.provinceList.size)
                }
            }

        })
    }
    fun getRegion(regionName : String): ArrayList<Province>{
        var index = regionName.indexOf(regionName)
        //default case
        if(index==-1)index = regionName.indexOf("Central")
         return regionList[index].provinceList
    }
}