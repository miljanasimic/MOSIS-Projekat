package elfak.mosis.petfinder.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.databinding.FragmentProfileBinding



class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeButtonEnabled(true)
        val editFirstName : ImageButton = binding.editFirstName
        val editLastName : ImageButton = binding.editLastName
        val editPhoneNumber : ImageButton = binding.editPhoneNumber
        val firstName : TextInputLayout = binding.firstNameInputLayout
        val lastName : TextInputLayout = binding.lastNameInputLayout
        val phoneNumber : TextInputLayout = binding.phoneNumberInputLayout

        val profilePhoto: ImageView = binding.profilePhoto
        val firstNameText: EditText = binding.firstname
        val lastNameText: EditText = binding.lastname
        val phoneNumberText: EditText = binding.phoneNumber
        val emailText: EditText = binding.email
        val newPasswordText: EditText = binding.newPassword

        val progressBar : ProgressBar = binding.progressBarEditProfile
        progressBar.visibility = View.VISIBLE
        profileViewModel.loadProfileData()
        val saveButton: Button = binding.buttonSave
        var calledSave = false

        profileViewModel.loginUser.observe(viewLifecycleOwner,
        Observer { user ->
            firstNameText.setText(user.firstName)
            lastNameText.setText(user.lastName)
            phoneNumberText.setText(user.phone)
            emailText.setText(user.email)
            if(user.imageBitmap!=null){
                profilePhoto.setImageBitmap(user.imageBitmap)
                profilePhoto.setColorFilter(Color.parseColor("#80000000"))
                profilePhoto.rotation= (90).toFloat()
            }
            progressBar.visibility=View.GONE
        })
        editFirstName.setOnClickListener {
            firstName.isEnabled = !firstName.isEnabled
            if(firstName.isEnabled) firstName.requestFocus() else firstName.clearFocus()
        }
        editLastName.setOnClickListener {
            lastName.isEnabled = !lastName.isEnabled
            if(lastName.isEnabled) lastName.requestFocus() else lastName.clearFocus()
        }
        editPhoneNumber.setOnClickListener {
            phoneNumber.isEnabled = !phoneNumber.isEnabled
            if(phoneNumber.isEnabled) phoneNumber.requestFocus() else phoneNumber.clearFocus()
        }
        saveButton.setOnClickListener {
            val user=profileViewModel.loginUser.value
            val params= mutableMapOf<String, Any>()
            if (user != null) {
                var hasError=false
                if(firstNameText.text.toString() != user.firstName){
                    if(firstNameText.text.isEmpty()){
                        binding.firstNameInputLayout.error="Please enter a valid first name"
                        hasError=true
                    }
                    else
                        params["firstName"]=firstNameText.text.toString()
                }
                if(lastNameText.text.toString() != user.lastName){
                    if(lastNameText.text.isEmpty()){
                        binding.lastNameInputLayout.error="Please enter a valid last name"
                        hasError=true
                    }
                    else
                        params["lastName"]=lastNameText.text.toString()
                }
                if(phoneNumberText.text.toString() != user.phone){
                    if (!profileViewModel.isPhoneValid(phoneNumberText.text.toString())){
                        binding.phoneNumberInputLayout.error="Please enter a valid phone number"
                        hasError=true
                    } else
                        params["phone"]=phoneNumberText.text.toString()
                }
                if(newPasswordText.text.isNotEmpty()){
                    if(!profileViewModel.isPasswordValid(newPasswordText.text.toString())){
                        binding.newPasswordInputLayout.error="Password must be at least 6 characters"
                        hasError=true
                    }
                }
                if(hasError)
                    return@setOnClickListener
                if(params.isEmpty() && newPasswordText.text.isEmpty()){
                    Toast.makeText(view.context, "Your profile details are not modified.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                binding.firstNameInputLayout.error=null
                binding.lastNameInputLayout.error=null
                binding.phoneNumberInputLayout.error=null
                binding.newPasswordInputLayout.error=null
                progressBar.visibility=View.VISIBLE
                calledSave=true
                profileViewModel.updateProfileData(params, newPasswordText.text.toString())
            }
        }

        profileViewModel.updateResult.observe(viewLifecycleOwner,
            Observer { result ->
                if(calledSave){
                    newPasswordText.text.clear()
                    progressBar.visibility=View.GONE
                    Toast.makeText(view.context, result, Toast.LENGTH_LONG).show()
                    calledSave=false
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        menu.getItem(0).isVisible = false
        menu.getItem(2).isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
    }
    override fun onStop() {
        super.onStop()
    }
}