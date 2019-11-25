package com.example.egci428_travelguide.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.egci428_travelguide.Activity.ProvinceListActivity
import com.example.egci428_travelguide.Activity.ProvincePlacesActivity
import com.example.egci428_travelguide.DataModel.Province
import com.example.egci428_travelguide.DataSource.RegionDataSource
import com.example.egci428_travelguide.R
import kotlinx.android.synthetic.main.listview_province.view.*

class ProvincesAdapter (var context: Context, var objects: ArrayList<Province>): BaseAdapter(){
    private var listData: ArrayList<Province> = objects

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        println("Show list!!")
        val province = listData[p0]
        val view: View
        if(p1==null){
            val layoutInflater = LayoutInflater.from(p2!!.context)
            view = layoutInflater.inflate(R.layout.listview_province, p2, false)
            val viewHolder = ViewHolder(view.provinceName)
            view.tag = viewHolder
        } else {
            view = p1
        }

        val viewHolder = view.tag as ViewHolder
        viewHolder.name.text = province.name.toString()

        //set change page
        view.setOnClickListener {
            val intent = Intent(context, ProvincePlacesActivity::class.java)
            //pass province name to next page
            intent.putExtra("province", province.name.toString());
            intent.putExtra("region", province.region)
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
    private class ViewHolder(val name: TextView)
}