package elfak.mosis.petfinder.ui.friends

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import elfak.mosis.petfinder.databinding.FragmentConnectFriendsBinding
import java.io.IOException
import java.util.*
//client
class ConnectFriendsFragment : Fragment() {
    private var _binding: FragmentConnectFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listAdapter : ConnectAdapter
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
        val devicesListView = binding.devicesListView
        val devices= ArrayList<BluetoothDevice>()
        listAdapter = ConnectAdapter(view.context, devices)
        devicesListView.adapter=listAdapter
        devicesListView.setOnItemClickListener { adapterView, view, pos, id ->
                val device=adapterView.getItemAtPosition(pos) as BluetoothDevice
                val connectThread = ConnectThread(device)
                connectThread.start()
        }

        val bluetoothManager: BluetoothManager? = ContextCompat.getSystemService(requireContext(), BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
        if (!bluetoothAdapter.isEnabled) enableBluetooth(view.context)
        checkLocationPermission()
        binding.buttonDiscover.setOnClickListener{
            binding.nobodyAvailable.visibility=View.GONE
            binding.progressBar.visibility=View.VISIBLE
            listAdapter.clear()
            listAdapter.notifyDataSetChanged()
            bluetoothAdapter.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            activity?.registerReceiver(receiver, filter)
            Handler().postDelayed({
                if(listAdapter.isEmpty){
                    binding.progressBar.visibility=View.GONE
                    binding.nobodyAvailable.visibility=View.VISIBLE
                }

            }, 2000)
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
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if(listAdapter.addDevice(device)){
                        binding.progressBar.visibility=View.GONE
                        binding.nobodyAvailable.visibility=View.GONE
                        listAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        try{
            activity?.unregisterReceiver(receiver)
        }catch (e: java.lang.Exception){
            print(e)
        }finally {
            bluetoothAdapter.cancelDiscovery()
            super.onDestroy()
        }

    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        }

        public override fun run() {
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                try{
                    socket.connect()
                    manageMyConnectedSocket(socket)
                } catch (e: Exception){
                    print(e)
                }
            }
        }

        private fun manageMyConnectedSocket(socket: BluetoothSocket) {

        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}