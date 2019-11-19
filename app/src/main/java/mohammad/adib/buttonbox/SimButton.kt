package mohammad.adib.buttonbox

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SimButton : FloatingActionButton {

    val keyName: String
        get() = tag.toString()

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    private fun setup() {
        setOnLongClickListener {
            val options = arrayOfNulls<String>(26)
            for (i in 0..25) {
                options[i] = "" + (i + 'A'.toInt()).toChar()
            }
            val selection = options.indexOf(ButtonBoxApp.instance.getKeyCode(keyName).toChar() + "")

            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.choose_binding))
            builder.setSingleChoiceItems(
                options,
                selection
            ) { dialog, which ->
                options[which]?.let {
                    if(!ButtonBoxApp.instance.updateBinding(
                        keyName,
                        it.toCharArray()[0].toInt()
                    )) {
                        Toast.makeText(context, "Key binding already used", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setPositiveButton(context.getString(R.string.done)) { dialog, which -> }
            builder.show()
            true
        }
    }
}
