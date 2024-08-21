package by.nikonov.testapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.nikonov.timetrackersdk.TimeTrackerSDK
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TimeTrackerSDK.attachActivity(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                TimeTrackerSDK.getTimeInSecondsFlow(this@MainActivity)
                    ?.map {
                        it / 1000
                    }
                    ?.onEach {
                        findViewById<TextView>(R.id.time_view).text =
                            getString(R.string.time_text, it.toString())
                    }?.collect()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(3000)
            TimeTrackerSDK.getTimeInSeconds(this@MainActivity)?.let {
                Snackbar.make(
                    findViewById<TextView>(R.id.time_view),
                    "time: $it millis",
                    600
                ).show()
            }
        }
    }
}