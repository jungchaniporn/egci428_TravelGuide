package com.example.egci428_travelguide.DataModel

class Place {
    var name: String
    var placeInfo: PlaceInfo
    //shorter form than prev ex
    constructor( n: String ="", info: PlaceInfo=PlaceInfo()){
        name = n
        placeInfo = info
    }
}