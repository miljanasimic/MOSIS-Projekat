package elfak.mosis.petfinder.ui.friends

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendsViewModel() : ViewModel() {
    private val db = Firebase.firestore

    fun createFriendship(user1: String, user2: String) {

    }
}