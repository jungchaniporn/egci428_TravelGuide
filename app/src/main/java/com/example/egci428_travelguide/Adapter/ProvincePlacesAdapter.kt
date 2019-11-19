package com.example.egci428_travelguide.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.egci428_travelguide.DataModel.Place
import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.R
import kotlinx.android.synthetic.main.listview_province.view.*
import kotlinx.android.synthetic.main.listview_provinceplace.view.*

class ProvincePlacesAdapter (var context: Context, var objects: ArrayList<Place>): BaseAdapter(){
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
        //set image
        //viewHolder.img =

        //set change page
//        view.setOnClickListener {
//            //delete
//            dataSource.deleteUser(user.id)
//            listData.removeAt(p0)
//            notifyDataSetChanged()
//        }
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