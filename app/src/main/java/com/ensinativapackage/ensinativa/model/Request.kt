package com.ensinativapackage.ensinativa.model

data class Request (
    val creatorDisplayName : String = "",
    val creatorUID : String = "",
    val imageSrc: String = "",
    var title : String = "",
    var description : String = "",
    var tag1 : String = "",
    var tag2 : String = "",
    var createdDate: String = "",
    var solved : Boolean = false,
    val solverUID : String = ""
)