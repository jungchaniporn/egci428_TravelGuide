package com.example.egci428_travelguide.DataModel

class Region (var name: String, var provinceList: ArrayList<Province>) {
    //shorter form than prev ex
    constructor(): this("",ArrayList<Province>())
}