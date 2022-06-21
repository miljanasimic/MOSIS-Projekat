package elfak.mosis.petfinder.data.model

import android.graphics.Bitmap

data class User(var firstName: String="",
var lastName: String="",
var phone: String="",
var imageUrl: String="",
var email: String="",
var imageBitmap: Bitmap? = null)
