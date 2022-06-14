package elfak.mosis.petfinder.ui.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentLoginBinding

import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.model.User

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emailEditText: EditText = binding.email
        val passwordEditText: EditText = binding.password
        val loginButton: Button = binding.login
        val loadingProgressBar = binding.loading

       binding.textRegister.setOnClickListener {
           findNavController().navigate(R.id.action_LoginFragment_to_RegistrationFragment)
       }

        loginButton.setOnClickListener {
//            if (emailEditText.text.isEmpty()) {
//                binding.emailTextInputLayout.error="E-mail address is required"
//            }
//            if (passwordEditText.text.isEmpty()) {
//                binding.passwordTextInputLayout.error="Password is required"
//            }
//            if( passwordEditText.text.isEmpty() || emailEditText.text.isEmpty())
//                return@setOnClickListener
//
//            loadingProgressBar.visibility = View.VISIBLE
//            loginViewModel.login(emailEditText.text.toString(),passwordEditText.text.toString())
            //TODO VRATI OVO ZAKOMENTARISANO
            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { result ->
                loadingProgressBar.visibility = View.GONE
                if (result.isSuccess) {
                    findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                } else {
                    result.message?.let { Toast.makeText(view.context, it, Toast.LENGTH_LONG).show() }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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