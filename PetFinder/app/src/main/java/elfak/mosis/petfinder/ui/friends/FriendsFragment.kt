package elfak.mosis.petfinder.ui.friends

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.databinding.FragmentFriendsBinding



class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeButtonEnabled(false)
        val tabLayoutFriends: TabLayout = binding.tabFriends
        setUpTabs()
        binding.tabFriends.setupWithViewPager(binding.viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(parentFragmentManager)
        adapter.addFragment(AllFriendsFragment(), "All")
        adapter.addFragment(FriendsRequestsFragment(), "Requests")
        adapter.addFragment(ConnectFriendsFragment(), "Connect")
        binding.viewPager.adapter=adapter
    }

}