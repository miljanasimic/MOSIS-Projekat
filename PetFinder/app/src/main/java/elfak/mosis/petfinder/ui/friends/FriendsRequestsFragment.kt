package elfak.mosis.petfinder.ui.friends

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentFriendsRequestsBinding
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

//server
class FriendsRequestsFragment : Fragment() {
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private var _binding: FragmentFriendsRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val REQUEST_CODE_ENABLE_BT = 1
    private val REQUEST_CODE_ENABLE_DISC = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bluetoothManager: BluetoothManager? = ContextCompat.getSystemService(requireContext(), BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
        if (!bluetoothAdapter.isEnabled) enableBluetooth(view.context)
        binding.buttonMakeDiscoverable.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) enableBluetooth(view.context)
            if (!bluetoothAdapter.isEnabled) return@setOnClickListener

            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
            startActivityForResult(discoverableIntent, REQUEST_CODE_ENABLE_DISC)
        }
    }
    private fun enableBluetooth(context: Context) {
        Toast.makeText(context, "In order to connect to other users, you have to enable Bluetooth.", Toast.LENGTH_SHORT).show()
        val enableBlIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBlIntent, REQUEST_CODE_ENABLE_BT)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_BT) {
            if( resultCode == Activity.RESULT_OK){
                if(bluetoothAdapter.isEnabled){
                    Toast.makeText(view?.context, "Bluetooth enabled.", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(view?.context, "Bluetooth is not enabled. Please try again", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(view?.context, "Cancelled", Toast.LENGTH_SHORT).show()
        } else if (requestCode == REQUEST_CODE_ENABLE_DISC) {
            if ( resultCode == 300) {
                val acceptThread = AcceptThread()
                acceptThread.start()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("PetFinder", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        }

        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        private fun manageMyConnectedSocket(socket: BluetoothSocket) {
            val connectionThread = AcceptedThread(socket)
            connectionThread.start()
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the connect socket", e)
            }
        }
    }

    private inner class AcceptedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                } finally {
                    if(mmBuffer.isNotEmpty()){
                        val friendId = mmBuffer.decodeToString()
                        friendsViewModel.createFriendship(friendId,
                            Firebase.auth.currentUser?.email.toString()
                        )

                    }
                }
            }
        }
        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}