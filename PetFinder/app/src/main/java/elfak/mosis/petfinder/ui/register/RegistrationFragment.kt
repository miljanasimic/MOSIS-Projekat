package elfak.mosis.petfinder.ui.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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
    private lateinit var currentPhotoPath: String
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 1

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by activityViewModels()

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
        val firstNameEditText: EditText = binding.firstname
        val lastNameEditText: EditText = binding.lastname
        val phoneEditText: EditText = binding.phone
        val emailEditText: EditText = binding.email
        val passwordEditText: EditText = binding.password
        val signUpButton: Button = binding.buttonSignUp
        val progressBar: ProgressBar = binding.progressBar

        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }
        val afterPasswordChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                if (passwordEditText.text.isNotEmpty() && passwordEditText.text.length<6){
                    binding.passwordTextInputLayout.setError("Password must be at least 6 characters")
                }
            }
        }
        passwordEditText.addTextChangedListener(afterPasswordChangedListener)

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
            if (firstNameEditText.text.isEmpty()) {
                binding.firstNameTextInputLayout.setError("First name is required")
            }
            if (lastNameEditText.text.isEmpty()) {
                binding.lastNameTextInputLayout.setError("Last name is required")
            }
            if (phoneEditText.text.isEmpty()) {
                binding.phoneNumberTextInputLayout.setError("Phone number is required")
            }
            if (emailEditText.text.isEmpty()) {
                binding.emailTextInputLayout.setError("E-mail address is required")
            }
            if (passwordEditText.text.isEmpty()) {
                binding.passwordTextInputLayout.setError("Password is required")
            }
            if (firstNameEditText.text.isEmpty()
                || lastNameEditText.text.isEmpty()
                || phoneEditText.text.isEmpty()
                || emailEditText.text.isEmpty()
                || passwordEditText.text.isEmpty())
                return@setOnClickListener

            if (!registerViewModel.isEmailValid(emailEditText.text.toString())){
                binding.emailTextInputLayout.error="Please enter a valid e-mail address"
                return@setOnClickListener
            }
            if (!registerViewModel.isPhoneValid(phoneEditText.text.toString())){
                binding.phoneNumberTextInputLayout.error="Please enter a valid phone number"
                return@setOnClickListener
            }
            //TODO ako ne unese sliku???
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
            binding.imageView.setImageBitmap(imageBitmap)
            binding.imageView.setColorFilter(Color.parseColor("#80000000"))
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