package com.example.egci428_travelguide.DataSource

import android.content.Context
import android.widget.ListView
import com.example.egci428_travelguide.Adapter.ProvincePlacesAdapter
import com.example.egci428_travelguide.Adapter.ProvincesAdapter
import com.example.egci428_travelguide.DataModel.Place
import com.example.egci428_travelguide.DataModel.PlaceInfo
import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.DataModel.Region
import com.google.firebase.database.*

class ProvincePlacesDataSource (provinceInName: String, listView: ListView, context: Context){
    val dataReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("province/$provinceInName/place")
    lateinit var placesList:ArrayList<Place>
    init{
        var tmp = ArrayList<Place>()
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    val placename = it.key
                    it.children.forEach{
                        if(it.key=="info") {
                            tmp.add(Place(placename!!,it.getValue(PlaceInfo::class.java)!!))
                        }
                    }
                }
                println("Done add data")
                println("Function ended")
                placesList = tmp
                //val regionReceived = getRegion(provinceInName)
                println("Places size "+placesList.size)
                val arrayAdapter = ProvincePlacesAdapter(context, placesList, provinceInName)
                listView.setAdapter(arrayAdapter)
                println("Done set adapter")
            }
        })
    }
}