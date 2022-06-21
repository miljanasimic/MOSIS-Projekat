package elfak.mosis.petfinder.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.model.User
import java.io.File

class ProfileViewModel : ViewModel() {
    private val loggedUser: FirebaseUser? = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val _loginUser = MutableLiveData<User>()
    var loginUser: LiveData<User> = _loginUser

    private val _updateResult = MutableLiveData<String>()
    var updateResult: LiveData<String> = _updateResult


    fun loadProfileData() {
        if (loggedUser != null) {
            db.collection("users").document(loggedUser.uid).get()
                .addOnSuccessListener { document ->
                    val userData = document.toObject<User>()
                    if (userData != null) {
                        if (userData.imageUrl.isNotEmpty()) {
                            val image = storage.getReferenceFromUrl(userData.imageUrl)
                            val localFile = File.createTempFile("tempImage", "jpg")
                            image.getFile(localFile)
                                .addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                    userData.imageBitmap = bitmap
                                    _loginUser.value = userData!!
                                }
                                .addOnFailureListener {
                                    userData.imageBitmap = null
                                    _loginUser.value = userData!!
                                }
                        } else {
                            _loginUser.value = userData!!
                        }
                    }
                }

        }
    }

    fun updateProfileData(paramsList: MutableMap<String, Any>, newPassword: String){
        loggedUser?.let { db.collection("users").document(it.uid).update(paramsList)
            .addOnSuccessListener {
                if(newPassword.isNotEmpty()){
                    loggedUser.updatePassword(newPassword)
                        .addOnSuccessListener {
                            _updateResult.value="Your profile has been successfully updated."
                        }
                } else {
                    _updateResult.value="Your profile has been successfully updated."
                }
            }
            .addOnFailureListener {
                _updateResult.value="Failed to update your profile. Please try again!"
            }}
    }
    fun isPhoneValid(phone: String): Boolean {
        return phone.length==8 || phone.length==9
    }
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}