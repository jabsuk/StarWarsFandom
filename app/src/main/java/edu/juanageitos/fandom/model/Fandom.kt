package edu.juanageitos.fandom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fandom(
    val id : Int,
    val name : String,
    val universe : String,
    val description : String,
    val image : String,
    val info : String,
    var fav : Boolean,
    var visible : Boolean
) : Parcelable