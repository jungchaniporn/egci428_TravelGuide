package com.example.egci428_travelguide.DataModel

class Province {
    var name: String
    var place: ArrayList<Place>
    var region: String
    var rating: Float
    //shorter form than prev ex
    constructor( n: String ="", reg: String="", rat: Float=0.toFloat()){
        name = n
        region = reg
        rating = rat
        place = ArrayList<Place>()
    }
}