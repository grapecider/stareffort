package mi191324.example.stareffort

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.util.*
import kotlin.collections.ArrayList


class Serviceclass : Service() {

    private var mTimer: Timer? = null
    var mHandler: Handler = Handler()

    override fun onCreate() {
        Log.i("TestService", "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("TestService", "onStartCommand")
        var appname: String
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                appgetter()
                val applist = printForegroundTask()
                mHandler.post {
                    Log.d("TestService", "Timer run")
                    Log.d("apps", applist.toString())
                }
            }
        }, 10000, 1000)
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i("TestService", "onDestroy")
    }

    override fun onBind(arg0: Intent): IBinder? {
        Log.i("TestService", "onBind")
        return null
    }

    fun appgetter() {
        val appList = ArrayList<String>()
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        //起動中アプリの取得
        val runningApp = activityManager.runningAppProcesses
        Log.d("pop", runningApp.toString())
        val packageManager = packageManager
        if (runningApp != null) {
            for (app in runningApp) {
                try {
                    // アプリ名をリストに追加
                    val appInfo = packageManager.getApplicationInfo(app.processName, 0)
                    appList.add(appInfo.toString())
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        val am = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = am.getRunningTasks(1)
        Log.d("dib", taskInfo.toString())
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo[0].topActivity!!.className)
        val componentInfo = taskInfo[0].topActivity
        componentInfo!!.packageName

        val mActiviyManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager


        // 現在稼働中のプロセスをLISTで取得
        val processList: MutableList<RunningAppProcessInfo>? = mActiviyManager.runningAppProcesses
        for (process in processList!!) {
            Log.d("TAG1", "pid:" + process.pid)
            Log.d("TAG2", "processName:" + process.processName)
        }
    }
    private fun printForegroundTask(): String? {
        var currentApp = "NULL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm = this.getSystemService("usagestats"!!) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                }
            }
        } else {
            val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }
        Log.e("adapter", "Current App in foreground is: $currentApp")
        return currentApp
    }
}

