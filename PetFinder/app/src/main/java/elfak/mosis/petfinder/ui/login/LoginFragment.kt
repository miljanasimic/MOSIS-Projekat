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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentLoginBinding

import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.model.User

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
//    private var emailEditText: EditText = binding.email
//    private var passwordEditText: EditText = binding.password

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
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        auth = Firebase.auth

        val emailEditText: EditText = binding.email
        val passwordEditText: EditText = binding.password
        val loginButton: Button = binding.login
        val loadingProgressBar = binding.loading
        loginButton.isEnabled=false

       binding.textRegister.setOnClickListener {
           findNavController().navigate(R.id.action_LoginFragment_to_RegistrationFragment)
       }
//        loginViewModel.loginFormState.observe(viewLifecycleOwner,
//            Observer { loginFormState ->
//                if (loginFormState == null) {
//                    return@Observer
//                }
//                loginButton.isEnabled = loginFormState.isDataValid
//                loginFormState.emailError?.let {
//                    emailEditText.error = getString(it)
//                }
//                loginFormState.passwordError?.let {
//                    passwordEditText.error = getString(it)
//                }
//            })
//
//        loginViewModel.loginResult.observe(viewLifecycleOwner,
//            Observer { loginResult ->
//                loginResult ?: return@Observer
//                loadingProgressBar.visibility = View.GONE
//                loginResult.error?.let {
//                    showLoginFailed(it)
//                }
//                loginResult.success?.let {
//                    updateUiWithUser(it)
//                }
//            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                loginButton.isEnabled= (emailEditText.text.isNotEmpty()) && (passwordEditText.text.isNotEmpty())
            }
        }
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
//        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(
//                    emailEditText.text.toString(),
//                    passwordEditText.text.toString()
//                )
//            }
//            false
//        }

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
//            loadingProgressBar.visibility = View.VISIBLE
//            auth.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
//                        println("createUserWithEmail:success")
//                    } else {
//                        toast("Incorrect username or password. Please try again.", Toast.LENGTH_LONG)
//                    }
//                    loadingProgressBar.visibility = View.GONE
//                }
//            loginViewModel.login(
//                emailEditText.text.toString(),
//                passwordEditText.text.toString()
//            )
        }
    }

    private fun toast(text: String, length: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, text, length).show()

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
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