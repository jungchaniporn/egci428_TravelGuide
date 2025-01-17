package com.example.egci428_travelguide.DataModel

import java.io.Serializable

class PlaceInfo :Serializable {
    public var uid: String =""
    public var address: String=""
    public var placeInfo: String=""
    public var tel: String=""
    public var images: ArrayList<String> = ArrayList<String>()
    //shorter form than prev ex
    constructor(){
        //default constructor for firebase
    }
    constructor( userid:String="", addr: String ="", info: String = "", phone:String="", imgs: ArrayList<String>){
        this.uid = userid
        this.address = addr
        this.placeInfo = info
        this.tel = phone
        this.images = imgs
    }
}