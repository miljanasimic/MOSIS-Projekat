package elfak.mosis.petfinder.ui.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.model.User
import elfak.mosis.petfinder.databinding.FragmentRegistrationBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private var currentPhotoPath = ""
    private lateinit var photoFile: File
    private var photoUri: Uri = Uri.EMPTY
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var profileImage: ImageView

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firstNameEditText: EditText = binding.firstname
        val lastNameEditText: EditText = binding.lastname
        val phoneEditText: EditText = binding.phone
        val emailEditText: EditText = binding.email
        val passwordEditText: EditText = binding.password
        val signUpButton: Button = binding.buttonSignUp
        val progressBar: ProgressBar = binding.progressBar
        profileImage = binding.imageView

        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }
        binding.camera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile()
            currentPhotoPath=photoFile.name
            photoFile.also {
                val appContext = context?.applicationContext ?: return@also
                photoUri = FileProvider.getUriForFile(
                    appContext,
                    "elfak.mosis.petfinder.fileprovider",
                    it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
        signUpButton.setOnClickListener{
            if (firstNameEditText.text.isEmpty())
                binding.firstNameTextInputLayout.error="First name is required"
            if (lastNameEditText.text.isEmpty())
                binding.lastNameTextInputLayout.error="Last name is required"
            if (phoneEditText.text.isEmpty())
                binding.phoneNumberTextInputLayout.error="Phone number is required"
            if (emailEditText.text.isEmpty())
                binding.emailTextInputLayout.error="E-mail address is required"
            if (passwordEditText.text.isEmpty())
                binding.passwordTextInputLayout.error="Password is required"
            if (firstNameEditText.text.isEmpty()
                || lastNameEditText.text.isEmpty()
                || phoneEditText.text.isEmpty()
                || emailEditText.text.isEmpty()
                || passwordEditText.text.isEmpty())
                return@setOnClickListener
            var hasError = false
            if (!registerViewModel.isPhoneValid(phoneEditText.text.toString())){
                binding.phoneNumberTextInputLayout.error="Please enter a valid phone number"
                hasError=true
            }
            if (!registerViewModel.isEmailValid(emailEditText.text.toString())){
                binding.emailTextInputLayout.error="Please enter a valid e-mail address"
                hasError=true
            }
            if (!registerViewModel.isPasswordValid(passwordEditText.text.toString())){
                binding.passwordTextInputLayout.error="Password must be at least 6 characters"
                hasError=true
            }
            if (hasError)
                return@setOnClickListener

            progressBar.setVisibility(View.VISIBLE)
            val user = User(firstNameEditText.text.toString(), lastNameEditText.text.toString(),
                phoneEditText.text.toString(),"",emailEditText.text.toString() )
            registerViewModel.register(user, passwordEditText.text.toString(),currentPhotoPath, photoUri)
        }

        registerViewModel.registerResult.observe(viewLifecycleOwner,
        Observer { result ->
            progressBar.visibility = View.GONE
            result.success?.let{
                Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
            }
            result.error?.let{
                Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun getPhotoFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            profileImage.setImageBitmap(imageBitmap)
            profileImage.setColorFilter(Color.parseColor("#80000000"))
        }
        else{
            currentPhotoPath=""
            photoUri= Uri.EMPTY
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}