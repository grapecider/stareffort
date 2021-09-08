package mi191324.example.stareffort

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.jaredrummler.android.processes.AndroidProcesses
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
                proceedapp()
                appgetter()
                mHandler.post {
                    Log.d("TestService", "Timer run")
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

    fun proceedapp() {
        //動いているアプリ一覧取得
        val processes = AndroidProcesses.getRunningAppProcesses()
        val applist: ArrayList<String> = ArrayList()
        Log.d("size", processes.size.toString())
        for (process in processes) {
            // Get some information about the process
            val processName = process.name
            val stat = process.stat()
            val pid = stat.pid
            val parentProcessId = stat.ppid()
            val startTime = stat.stime()
            val policy = stat.policy()
            val state = stat.state()
            val statm = process.statm()
            val totalSizeOfProcess = statm.size
            val residentSetSize = statm.residentSetSize
            val packageInfo = process.getPackageInfo(this, 0)
            val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            Log.d("appnamefor", appName)
            applist.add(appName)
        }
        Log.d("applist", applist.toString())
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
        /*val pm: PackageManager = getPackageManager()
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName)
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
        }*/
        /*val amm = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.getRecentTasks(1, ActivityManager.RECENT_WITH_EXCLUDED)
        val i = l.iterator()
        val pm = packageManager
        while (i.hasNext()) {
            val info = i.next() as RunningAppProcessInfo
            try {
                val c = pm.getApplicationLabel(
                    pm.getApplicationInfo(
                        info.processName, PackageManager.GET_META_DATA
                    )
                )
                Log.w("LABEL", c.toString())
            } catch (e: Exception) {
            }
        }*/
    }
}

