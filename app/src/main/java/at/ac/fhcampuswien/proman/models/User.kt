package at.ac.fhcampuswien.proman.models



data class User(
    val id: String = "",
    val email: String = "",
    val name : String = "",
    var selected: Boolean = false
)
