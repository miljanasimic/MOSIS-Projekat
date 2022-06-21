package elfak.mosis.petfinder.ui.rank

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.databinding.FragmentRankBinding


class RankFragment : Fragment() {

    private val rankViewModel: RankViewModel by activityViewModels()
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeButtonEnabled(false)

        val progressBar = binding.progressBarRank
        progressBar.visibility=View.VISIBLE
        val usersListView = binding.usersListView
        val personalRankInfo = binding.personalRank
        personalRankInfo.itemUserRank.visibility=View.GONE
        rankViewModel.loadAllUsers()
        rankViewModel.users.observe(viewLifecycleOwner,
        Observer { users ->
            val listAdapter = UserAdapter(view.context, users)
            usersListView.adapter = listAdapter
            val currUser=Firebase.auth.currentUser
            if(currUser!=null){
                val thisUser= users.find { it.email== currUser.email }

                if(thisUser!=null){
                    personalRankInfo.userEmail.text=thisUser.email
                    personalRankInfo.userFullName.text=thisUser.firstName.plus(" ").plus(thisUser.lastName)
                    personalRankInfo.userRank.text=thisUser.rank.toString()
                    personalRankInfo.itemUserRank.visibility=View.VISIBLE
                    if(thisUser.imageBitmap!=null){
                        personalRankInfo.userPhoto.setImageBitmap(thisUser.imageBitmap)
                        personalRankInfo.userPhoto.setColorFilter(Color.parseColor("#80000000"))
                        personalRankInfo.userPhoto.rotation= (90).toFloat()
                    }
                }
            }

            progressBar.visibility=View.GONE

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile-> {
                this.findNavController().navigate(R.id.action_RankFragment_to_ProfileFragment)
                true
            }
            R.id.action_logout -> {
                Firebase.auth.signOut()
                this.findNavController().navigate(R.id.action_RankFragment_to_WelcomeFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}