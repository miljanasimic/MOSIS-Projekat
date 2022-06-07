package elfak.mosis.petfinder.ui.register

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentRegistrationBinding? = null
    val REQUEST_IMAGE_CAPTURE = 1
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val firstNameEditText: EditText = binding.firstname
        val lastNameEditText: EditText = binding.lastname
        val phoneEditText: EditText = binding.phone
        val emailEditText: EditText = binding.email
        val passwordEditText: EditText = binding.password
        val signUpButton: Button = binding.buttonSignUp
        signUpButton.isEnabled=false
        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                signUpButton.isEnabled= (firstNameEditText.text.isNotEmpty())
                        && (lastNameEditText.text.isNotEmpty())
                        && (phoneEditText.text.isNotEmpty())
                        && (emailEditText.text.isNotEmpty())
                        && (passwordEditText.text.isNotEmpty())
            }
        }
        firstNameEditText.addTextChangedListener(afterTextChangedListener)
        lastNameEditText.addTextChangedListener(afterTextChangedListener)
        phoneEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        binding.camera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }

        }
        signUpButton.setOnClickListener{
            auth.createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = auth.currentUser
                        println("createUserWithEmail:success")

                    } else {
                        println("Authentication failed.")
                    }
                } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
            binding.imageView.setColorFilter(Color.parseColor("#80000000"))
        }
    }

}