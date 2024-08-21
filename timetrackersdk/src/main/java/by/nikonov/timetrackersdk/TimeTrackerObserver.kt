package by.nikonov.timetrackersdk

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Timer
import java.util.TimerTask

private const val SHARED_PREFERENCES_FILE_KEY = "by.nikonov.timetrackersdk.PREFERENCES_FILE_KEY"
private const val PERIOD_MILLIS = 100L

internal class TimeTrackerObserver :
    DefaultLifecycleObserver {

    val timeFlow get() = _timeFlow.asStateFlow()
    private val _timeFlow = MutableStateFlow<Long>(0)

    private val timer = Timer()
    private var task: TimerTask? = null

    override fun onStart(owner: LifecycleOwner) {
        (owner as? Context)?.let {
            _timeFlow.value = getSharedPreferences(it).getLong(it.javaClass.simpleName, 0)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        createTask()
        timer.schedule(
            task,
            0,
            PERIOD_MILLIS
        )
    }

    override fun onPause(owner: LifecycleOwner) {
        task?.cancel()
    }

    override fun onStop(owner: LifecycleOwner) {
        (owner as? AppCompatActivity)?.let {
            getSharedPreferences(it).edit(true) {
                putLong(it.javaClass.simpleName, _timeFlow.value)
            }
        }
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    private fun createTask() {
        task = object : TimerTask() {
            override fun run() {
                _timeFlow.update { it + PERIOD_MILLIS }
            }

        }
    }
}