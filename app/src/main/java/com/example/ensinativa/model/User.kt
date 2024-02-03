package com.example.ensinativa.model

data class User (
    var uid : String = "",
    var displayName : String = "",
    var email : String = "",
    var description : String = "",
    var about : String = "",
    var achievements : List<Achievement> = emptyList(),
    var tags : List<String> = emptyList(),
    val imageSrc : String = ""
)
