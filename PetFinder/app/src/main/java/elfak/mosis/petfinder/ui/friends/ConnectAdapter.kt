package elfak.mosis.petfinder.ui.friends

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import elfak.mosis.petfinder.R

class ConnectAdapter(private val context: Context, private val dataSource: ArrayList<BluetoothDevice>): BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var device: BluetoothDevice? = null
    override fun getCount(): Int {
        return dataSource.size
    }
    override fun getItem(p: Int): BluetoothDevice {
        return dataSource[p]
    }
    override fun getItemId(p: Int): Long {
        return p.toLong()
    }
    @SuppressLint("MissingPermission")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_user_device, parent, false)
        val deviceName = rowView.findViewById<TextView>(R.id.device_name)
        val device = getItem(position)
        deviceName.text = device.name
        val sendRequest = rowView.findViewById<ImageView>(R.id.send_request_icon)
        sendRequest.setOnClickListener { this.device = device }
        return rowView
    }

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice) : Boolean{
        val foundedDevice=dataSource.find { d -> d.name==device.name && d.address==device.address }
        if (foundedDevice!=null)
            return false
        dataSource.add(device)
        return true
    }
    fun clear() {
        dataSource.clear()
    }

    fun getClickedDevice(): BluetoothDevice? {
        return this.device
    }
}