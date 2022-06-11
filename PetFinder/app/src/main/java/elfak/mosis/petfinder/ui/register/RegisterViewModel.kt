package elfak.mosis.petfinder.ui.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.Result
import elfak.mosis.petfinder.ui.login.LoggedInUserView
import elfak.mosis.petfinder.ui.login.LoginResult

class RegisterViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    fun register(firstName: String, lastName: String, phoneNumber: String,email: String, password: String ) {
        //TODO dodati sliku
        auth = Firebase.auth

    }
}