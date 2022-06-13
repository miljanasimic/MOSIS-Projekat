package elfak.mosis.petfinder.ui.register

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.model.User

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val _registerResult = MutableLiveData<RegisterResult>()
    //private val _registerResult by lazy { MutableLiveData<RegisterResult>() }
    val registerResult : LiveData<RegisterResult> = _registerResult

    fun register(user: User, password: String, currentPhotoPath: String, photoUri: Uri) {

        val imageRef: StorageReference = storageRef.child("images/$currentPhotoPath")
        val uploadTask=imageRef.putFile(photoUri)
        uploadTask.addOnFailureListener {e->
            _registerResult.value= RegisterResult(error = "Image upload failed. Please try again.")
            //_registerResult.value=RegisterResult.Failure("Image upload failed. Please try again.")
            Log.w(ContentValues.TAG, "Error uploading image", e)
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener {
                user.imageUrl=it.toString()
                insertUserData(user, password)
            }
        }.addOnFailureListener{e->
            _registerResult.value= RegisterResult(error = "Registration failed. Please try again.")
            //_registerResult.value=RegisterResult.Failure("Registration failed. Please try again.")
            Log.w(ContentValues.TAG, "Error firebase sign-up", e)
        }
    }

    private fun insertUserData(user: User, password: String){
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            _registerResult.value= RegisterResult(success = "\"Your account has been successfully created! Please log in.")
                            //_registerResult.value=RegisterResult.Success("Your account has been successfully created! Please log in.")
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            _registerResult.value= RegisterResult(error = "Registration failed. Please try again.")
                            //_registerResult.value=RegisterResult.Failure("Registration failed. Please try again.")
                            Log.w(ContentValues.TAG, "Error adding document", e)
                            //TODO obrisati iz baze usera??
                        }
                }
            }
    }
}