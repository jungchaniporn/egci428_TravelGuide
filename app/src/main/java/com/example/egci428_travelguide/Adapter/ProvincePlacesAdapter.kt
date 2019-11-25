package com.example.egci428_travelguide.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.egci428_travelguide.Activity.PlaceInfoActivity
import com.example.egci428_travelguide.Activity.ProvincePlacesActivity
import com.example.egci428_travelguide.DataModel.Place
import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.R
import com.example.egci428_travelguide.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_place_info.*
import kotlinx.android.synthetic.main.listview_province.view.*
import kotlinx.android.synthetic.main.listview_provinceplace.view.*

class ProvincePlacesAdapter (var context: Context, var objects: ArrayList<Place>, var provinceInName: String, var regionInName: String): BaseAdapter(){
    private var listData: ArrayList<Place> = objects

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        println("Show list!!")
        val place = listData[p0]
        val view: View
        if(p1==null){
            val layoutInflater = LayoutInflater.from(p2!!.context)
            view = layoutInflater.inflate(R.layout.listview_provinceplace, p2, false)
            val viewHolder = ViewHolder(view.placeName, view.placeImage)
            view.tag = viewHolder
        } else {
            view = p1
        }

        val viewHolder = view.tag as ViewHolder
        viewHolder.name.text = place.name.toString()
        //set image if there is a image
        if(place.placeInfo.images.size > 0){
            val storageReference = FirebaseStorage.getInstance().reference
            //get image
            storageReference!!.child(place.placeInfo.images.get(0))
                .downloadUrl.addOnSuccessListener {
                Picasso
                    .get()
                    .load(it)
                    .fit()
                    .into(viewHolder.img);
            }.addOnFailureListener {
                // Handle any errors
                println("Set image unsuccessful")
            }
        }


        //set change page
        view.setOnClickListener {
            //to specific place page
            val intent = Intent(context, PlaceInfoActivity::class.java)
            //pass province name amnd place name to next page
            intent.putExtra("province", provinceInName);
            intent.putExtra("place", place.name);
            intent.putExtra("region", regionInName);
            //start next page
            context.startActivity(intent)
            val activity = context as Activity
            activity.finish()
        }
        return view
    }

    override fun getItem(p0: Int): Any {
        return listData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return listData.size
    }
    private class ViewHolder(val name: TextView, val img: ImageView)
}