package by.nikonov.timetrackersdk

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

private const val SHARED_PREFERENCES_FILE_KEY = "by.nikonov.timetrackersdk.PREFERENCES_FILE_KEY"

internal class TimeTrackerObserver :
    DefaultLifecycleObserver {

    private var previousTime = 0L
    private var startTime = 0L

    override fun onStart(owner: LifecycleOwner) {
        (owner as? Context)?.let {
            previousTime = getSharedPreferences(it).getLong(it.javaClass.simpleName, 0)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        startTime = System.currentTimeMillis()
    }

    override fun onPause(owner: LifecycleOwner) {
        previousTime = getTime()
    }

    override fun onStop(owner: LifecycleOwner) {
        (owner as? AppCompatActivity)?.let {
            getSharedPreferences(it).edit(true) {
                putLong(it.javaClass.simpleName, getTime())
            }
        }
    }

    fun getTime() = System.currentTimeMillis() - startTime + previousTime

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }
}