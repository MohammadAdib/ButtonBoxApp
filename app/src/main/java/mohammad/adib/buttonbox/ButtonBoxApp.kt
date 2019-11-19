package mohammad.adib.buttonbox

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ButtonBoxApp : Application() {

    private lateinit var preferences: SharedPreferences
    private var ip: InetAddress? = null
    private val port = 18250
    private var serverName: String? = null

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

    fun setServer(address: InetAddress) {
        ip = address
    }

    fun setServerName(name: String) {
        serverName = name
    }

    fun isConnected(): Boolean {
        return ip != null
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
}
