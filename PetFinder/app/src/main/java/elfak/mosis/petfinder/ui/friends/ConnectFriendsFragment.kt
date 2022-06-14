package elfak.mosis.petfinder.ui.friends

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import elfak.mosis.petfinder.databinding.FragmentConnectFriendsBinding

class ConnectFriendsFragment : Fragment() {
    private var _binding: FragmentConnectFriendsBinding? = null
    private val binding get() = _binding!!
    private var  bAdapter: BluetoothAdapter? =null
    private val REQUEST_CODE_ENABLE_BT = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bAdapter!!.isEnabled)
            print("Bl on")
        else
            print("Bl off")
        binding.buttonTurnOnBluetooth.setOnClickListener {
            if (bAdapter!!.isEnabled)
                Toast.makeText(view.context, "vec ukljucen", Toast.LENGTH_SHORT).show()
            else{
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }
        binding.buttonDiscover.setOnClickListener{
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_BT) {
            if( resultCode == Activity.RESULT_OK){
                if(bAdapter!!.isEnabled)
                    Toast.makeText(view?.context, "Dozvoljen", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(view?.context, "Nije Dozvoljen", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(view?.context, "cancelled", Toast.LENGTH_SHORT).show()

        }
    }

}