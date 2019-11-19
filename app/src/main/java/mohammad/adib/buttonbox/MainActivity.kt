package mohammad.adib.buttonbox

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var vibrator: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        findServer()
    }

    private fun findServer() {
        val dialog = ServerDialog()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "server")
    }

    fun buttonPress(v: View) {
        ButtonBoxApp.instance.dispatchButtonPress(v.tag.toString())
        vibrate()
    }

    private fun vibrate() {
        vibrator?.vibrate(25)
    }
}
