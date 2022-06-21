package elfak.mosis.petfinder.ui.rank

import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.model.User
import elfak.mosis.petfinder.data.model.UserRank
import java.io.File

class RankViewModel : ViewModel() {
    private val loggedUser: FirebaseUser? = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private val _users = MutableLiveData<List<UserRank>>()
    var users: LiveData<List<UserRank>> = _users

    fun loadAllUsers(){
        db.collection("users").orderBy("rank", Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            val allUsers=documents.toObjects<UserRank>()
            for (user in allUsers){
                if(user.imageUrl.isNotEmpty()){
                    val image = storage.getReferenceFromUrl(user.imageUrl)
                    val localFile = File.createTempFile("tempImage", "jpg")
                    image.getFile(localFile)
                        .addOnSuccessListener {
                            user.imageBitmap=BitmapFactory.decodeFile(localFile.absolutePath)
                            _users.value=allUsers
                        }
                        .addOnFailureListener {
                            user.imageBitmap=null
                            _users.value=allUsers
                        }
                }
            }
            _users.value=documents.toObjects<UserRank>()
        }
    }
}