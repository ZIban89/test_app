package by.nikonov.timetrackersdk

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val SHARED_PREFERENCES_FILE_KEY = "by.nikonov.timetrackersdk.PREFERENCES_FILE_KEY"
private const val PERIOD_MILLIS = 100L

internal class TimeTrackerObserver :
    DefaultLifecycleObserver {

    val timeFlow get() = _timeFlow.asStateFlow()
    private val _timeFlow = MutableStateFlow<Long>(0)
    private var job: Job? = null
    private var previousTime = 0L

    override fun onStart(owner: LifecycleOwner) {
        (owner as? Context)?.let {
            _timeFlow.value = getSharedPreferences(it).getLong(it.javaClass.simpleName, 0)

        }
    }

    override fun onResume(owner: LifecycleOwner) {
        job = owner.lifecycleScope.launch(Dispatchers.IO) {
            previousTime = System.currentTimeMillis()
            while (isActive) {
                delay(PERIOD_MILLIS)
                val time = System.currentTimeMillis()
                _timeFlow.update { it + time - previousTime }
                previousTime = time
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        job?.cancel()
        _timeFlow.update { it + System.currentTimeMillis() - previousTime }
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
}