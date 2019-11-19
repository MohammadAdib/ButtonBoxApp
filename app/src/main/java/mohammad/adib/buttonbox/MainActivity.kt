package mohammad.adib.buttonbox

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.text.SpannableString
import android.text.util.Linkify
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), Observer<String> {

    private var serverDialog: AlertDialog? = null
    private var vibrator: Vibrator? = null
    private var timer = Timer()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        ButtonBoxApp.instance.serverNameLiveData.observe(this, this)
        timer.scheduleAtFixedRate(Heartbeat(), 0, 10000)
    }

    override fun onChanged(name: String?) {
        serverName.text = if (name.isNullOrEmpty()) getString(R.string.not_connected) else name
        serverName.setCompoundDrawablesWithIntrinsicBounds(
            if (name.isNullOrEmpty()) R.drawable.red_dot else R.drawable.green_dot,
            0,
            0,
            0
        )
        if (name.isNullOrEmpty()) {
            if (serverDialog == null) {
                val s = SpannableString(getString(R.string.download_server))
                Linkify.addLinks(s, Linkify.ALL)
                serverDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_server_found))
                    .setMessage(s)
                    .create()
            }
            serverDialog?.let { if (!it.isShowing) it.show() }
        } else {
            serverDialog?.let { if (it.isShowing) it.dismiss() }
        }
    }

    fun buttonPress(v: View) {
        ButtonBoxApp.instance.dispatchButtonPress(v.tag.toString())
        vibrate()
    }

    fun displayInfo(v: View) {
        if (v.id == R.id.serverName) {
            if (!ButtonBoxApp.instance.connected) serverDialog?.let { if (!it.isShowing) it.show() }
        } else {
            val s = SpannableString(getString(R.string.info))
            Linkify.addLinks(s, Linkify.ALL)
            AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(s)
                .show()
        }
    }

    private fun vibrate() {
        vibrator?.vibrate(25)
    }

    private class Heartbeat : TimerTask() {
        override fun run() {
            ButtonBoxApp.instance.startSearch()
        }
    }
}
