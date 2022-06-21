package elfak.mosis.petfinder.data.model

import android.graphics.Bitmap

data class UserRank(var firstName: String="",
                    var lastName: String="",
                    var email: String="",
                    val imageUrl: String="",
                    val rank: Int=0,
                    var imageBitmap: Bitmap? = null)
