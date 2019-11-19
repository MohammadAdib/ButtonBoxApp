package mohammad.adib.buttonbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_server.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class ServerDialog : DialogFragment() {

    private val ip = InetAddress.getByName("255.255.255.255")
    private val port = 18250
    private var searching = false
    val socket = DatagramSocket(port)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSearch()
    }

    private fun startSearch() {
        Thread {
            var tries = 0
            while (searching && tries < 5) {
                val socket = DatagramSocket()
                val data = "buttonbox-ack".toByteArray()
                val packet = DatagramPacket(data, data.size, ip, port)
                socket.send(packet)
                Thread.sleep(500)
                tries++
            }
            searching = false
            activity?.runOnUiThread { finish() }
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
                    ButtonBoxApp.instance.setServer(packet.address)
                    ButtonBoxApp.instance.setServerName(message)
                    searching = false
                }
            }
        }.start()
    }

    private fun finish() {
        if (!ButtonBoxApp.instance.isConnected()) {
            progress.visibility = View.GONE
            searchingText.setText(getString(R.string.no_servers))
            retry.visibility = View.VISIBLE
            useBroadcast.visibility = View.VISIBLE
            retry.setOnClickListener {
                retry.visibility = View.GONE
                progress.visibility = View.VISIBLE
                searchingText.setText(getString(R.string.searching_for_servers))
                startSearch()
            }
            useBroadcast.setOnClickListener {
                ButtonBoxApp.instance.setServer(InetAddress.getByName("255.255.255.255"))
                ButtonBoxApp.instance.setServerName("Broadcast")
                socket.close()
                dismiss()
            }
        } else {
            socket.close()
            dismiss()
        }
    }
}
