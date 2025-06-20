package Notifications

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    private val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"

    fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestNotificationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(NOTIFICATION_PERMISSION), REQUEST_CODE)
    }

    // Function to check if exact alarms are allowed
    fun hasExactAlarmPermission(): Boolean {
        // Only relevant for Android 12 (API level 31) and above
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // For Android versions below 12, exact alarms are allowed by default
        }
    }

    // Function to prompt user to enable exact alarms in settings
    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            activity.startActivity(intent)
        }
    }

    companion object {
        const val REQUEST_CODE = 1001
    }
}
