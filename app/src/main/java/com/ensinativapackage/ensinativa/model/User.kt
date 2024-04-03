package com.ensinativapackage.ensinativa.model

data class User (
    var uid : String = "",
    var displayName : String = "",
    var email : String = "",
    var description : String = "",
    var achievements : List<Achievement> = emptyList(),
    var tags : List<String> = emptyList(),
    var imageSrc : String = "",
    var rating : Double = 0.0
)
