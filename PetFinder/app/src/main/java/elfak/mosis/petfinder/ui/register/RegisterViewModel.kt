package elfak.mosis.petfinder.ui.register

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.util.Patterns
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
    val registerResult : LiveData<RegisterResult> = _registerResult

    fun register(user: User, password: String, currentPhotoPath: String, photoUri: Uri) {
        if (currentPhotoPath.isEmpty()){
            //TODO dodati useru putanju za default-nu sliku
            insertUserData(user, password)
        } else{
            val imageRef: StorageReference = storageRef.child("images/$currentPhotoPath")
            val uploadTask=imageRef.putFile(photoUri)
            uploadTask.addOnFailureListener {e->
                _registerResult.value= RegisterResult(error = "Image upload failed. Please try again.")
                Log.w(ContentValues.TAG, "Error uploading image", e)
            }.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener {
                    user.imageUrl=it.toString()
                    insertUserData(user, password)
                }.addOnFailureListener{e->
                    _registerResult.value= RegisterResult(error = "Image upload failed. Please try again.")
                    Log.w(ContentValues.TAG, "Error firebase sign-up", e)
                }
            }
        }
    }

    private fun insertUserData(user: User, password: String){
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            _registerResult.value= RegisterResult(success = "Your account has been successfully created! Please log in.")
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            _registerResult.value= RegisterResult(error = "Registration failed. Please try again.")
                            Log.w(ContentValues.TAG, "Error adding document", e)
                            storage.getReferenceFromUrl(user.imageUrl).delete()
                                .addOnSuccessListener {}.addOnFailureListener {}
                        }
                } else {
                    _registerResult.value= RegisterResult(error =  task.exception?.message)
                    Log.w(ContentValues.TAG, "Error adding document", task.exception)
                    storage.getReferenceFromUrl(user.imageUrl).delete()
                        .addOnSuccessListener {}.addOnFailureListener {}
                }
            }
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isPhoneValid(phone: String): Boolean {
        return phone.length==8 || phone.length==9
    }
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

}