package mohammad.adib.buttonbox

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ButtonBoxApp : Application() {

    private lateinit var preferences: SharedPreferences
    private val broadcast = InetAddress.getByName("255.255.255.255")
    private var ip: InetAddress? = null
    var serverNameLiveData = MutableLiveData<String>()
    private var searching = false
    var connected = false
    private val port = 18250
    private val socket = DatagramSocket(port)

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!preferences.contains("A")) setupDefaultMapping()
    }

    companion object {
        lateinit var instance: ButtonBoxApp
            private set
    }

    private fun setServer(address: InetAddress?) {
        ip = address
    }

    private fun setServerName(name: String?) {
        serverNameLiveData.postValue(name)
    }

    private fun setupDefaultMapping() {
        preferences.edit()
            .putInt("A", 'A'.toInt())
            .putInt("B", 'B'.toInt())
            .putInt("X", 'X'.toInt())
            .putInt("Y", 'Y'.toInt())
            .putInt("plus_red", 'Q'.toInt())
            .putInt("minus_red", 'W'.toInt())
            .putInt("plus_yellow", 'E'.toInt())
            .putInt("minus_yellow", 'R'.toInt())
            .putInt("plus_blue", 'T'.toInt())
            .putInt("minus_blue", 'Y'.toInt())
            .apply()
    }

    private fun getKeyCode(tag: String): Int {
        return preferences.getInt(tag, 0)
    }

    fun dispatchButtonPress(tag: String) {
        val code = getKeyCode(tag).toString()
        ip?.let {
            Thread {
                val socket = DatagramSocket()
                val data = code.toByteArray()
                val packet = DatagramPacket(data, data.size, ip, port)
                socket.send(packet)
            }.start()
        }
    }

    fun startSearch() {
        Thread {
            var tries = 0
            connected = false
            while (searching && tries < 5) {
                val socket = DatagramSocket()
                val data = "buttonbox-ack".toByteArray()
                val packet = DatagramPacket(data, data.size, broadcast, port)
                socket.send(packet)
                Thread.sleep(500)
                tries++
            }
            searching = false
            if (!connected) {
                setServer(null)
                setServerName(null)
            }
        }.start()
        searching = true
        receiveResponses()
    }

    private fun receiveResponses() {
        Thread {
            while (searching) {
                val buffer = ByteArray(2048)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val message = String(buffer).toCharArray()
                    .filter {
                        it.isLetterOrDigit() ||
                                it == '-' || it == '_' || it == ' ' || it == '|'
                    }
                    .joinToString(separator = "")
                if (!message.startsWith("buttonbox-ack")) {
                    setServer(packet.address)
                    setServerName(message)
                    searching = false
                    connected = true
                }
            }
        }.start()
    }
}
