package com.example.egci428_travelguide.DataModel

class Province {
    var name: String
    var place: ArrayList<Place>
    var region: String
    //shorter form than prev ex
    constructor( n: String ="", reg: String=""){
        name = n
        region = reg
        place = ArrayList<Place>()
    }
}