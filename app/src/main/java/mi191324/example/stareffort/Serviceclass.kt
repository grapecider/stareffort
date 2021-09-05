package mi191324.example.stareffort


import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
        var appname:String
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                appname = proceedapp()
                appgetter()
                mHandler.post {
                    Log.d("TestService", "Timer run")
                    Log.d("nowappname", appname)
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

    fun proceedapp(): String {
        //動いているアプリ一覧取得
        val processes = AndroidProcesses.getRunningAppProcesses()
        var appName:String = "null"
        val applist:ArrayList<String> = ArrayList()

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
            appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            applist.add(appName)
        }
        Log.d("applist", applist.toString())
        return appName
    }

    fun appgetter(){
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

        // 現在稼働中のプロセスをLISTで取得
        /*val processList = mActiviyManager.runningAppProcesses
        for (process in processList) {
            Log.i(TAG, "pid:" + process.pid)
            Log.i(TAG, "processName:" + process.processName)
        }*/
        val endCal = Calendar.getInstance()
        val beginCal = Calendar.getInstance()
        val mActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcessInfoList: List<*> = mActivityManager.getRunningServices(Int.MAX_VALUE)
        Log.d("sip", appProcessInfoList.toString())
    }

    /**現在画面に表示されているアプリの情報を返す。 */
    val foregroundAppInfo: RunningAppProcessInfo?
        get() {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val appInfoList = activityManager.runningAppProcesses
            for (appInfo in appInfoList) {
                if (appInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && !isRunningService(appInfo.processName)
                ) {
                    Log.d("jack", appInfo.toString())
                    return appInfo
                }
            }
            return null
        }

    /**もしプロセスがサービスならtrueを返す。 */
    private fun isRunningService(processName: String?): Boolean {
        if (processName == null) return false
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val serviceInfoList: List<ActivityManager.RunningServiceInfo> =
            activityManager.getRunningServices(Int.MAX_VALUE)
        for (serviceInfo in serviceInfoList) {
            if (serviceInfo.process.equals(processName)) return true
        }
        return false
    }
}

enum class AppProcessStatus {
    /** 最前面で起動中 */
    FOREGROUND,
    /** バックグラウンド状態 */
    BACKGROUND,
    /** プロセスが存在しない */
    GONE;

    companion object {
        /** 現在のアプリのプロセスの状態を取得する */
        fun current(context: Context): AppProcessStatus {
            if (!existsAppTask(context)) return GONE
            if (isForeground(context)) return FOREGROUND
            return BACKGROUND
        }

        private fun isForeground(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                for (activeProcess in processInfo.pkgList) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true
                    }
                }
            }
            return false
        }

        private fun existsAppTask(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return am.appTasks.count() > 0
            }
            val recentTasks = am.getRunningTasks(Integer.MAX_VALUE)
            for (i in recentTasks.indices) {
                if (recentTasks[i].baseActivity!!.packageName == context.packageName) {
                    return true
                }
            }
            return false
        }
    }
}