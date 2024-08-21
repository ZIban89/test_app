package by.nikonov.timetrackersdk

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.StateFlow

object TimeTrackerSDK {

    private val activityObservers =
        mutableMapOf<Class<out AppCompatActivity>, TimeTrackerObserver>()

    fun attachActivity(activity: AppCompatActivity) {
        val observer = TimeTrackerObserver()
        activityObservers[activity.javaClass] = observer
        activity.lifecycle.addObserver(observer)
    }

    fun getTimeInSeconds(activity: AppCompatActivity): Long? {
        return getTimeInSecondsFlow(activity)?.value
    }

    fun getTimeInSecondsFlow(activity: AppCompatActivity): StateFlow<Long>? {
        return activityObservers[activity.javaClass]?.timeFlow
    }
}