package mi191324.example.stareffort

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


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
                val forapp = printForegroundTask()
                val prefapplist = getprefapps()
                val pkgs = allappget()
                mHandler.post {
                    Log.d("TestService", "Timer run")
                    for (app in pkgs){
                        if (app == forapp.toString()){
                            Log.d("下ネタ", "tinko")
                        }
                    }
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

    //フォアグランドアプリ取得
    private fun printForegroundTask(): String? {
        val applist = allappget()
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

    //pref保存したArrayListを呼び出す関数
    fun getprefapps(): ArrayList<*> {
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val gson = Gson()
        val NameList: ArrayList<*> = gson.fromJson(
            shardPreferences.getString("applist", "[]"),
            object : TypeToken<List<*>>() {}.type
        )
        var applist = arrayListOf<String>()
        var i:Int = 1
        while (i < NameList.size){
            if (NameList.get(i).toString() == "false"){
                i -= 1
                applist.add(NameList.get(i).toString())
                i += 1
            }
            i += 2
        }
        return applist
    }
    //インストール済みアプリ取得
    fun allappget() : ArrayList<String>{
        val arraylist = arrayListOf<String>()
        val prefapplist = getprefapps()
        val allapps = this.packageManager.getInstalledApplications(0)
        for (appInfo in allapps) {
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != ApplicationInfo.FLAG_SYSTEM) {
                val appname = displayName(appInfo)
                for (prefapp in prefapplist){
                    if (appname == prefapp){
                        arraylist.add(appInfo.packageName.toString())
                    }
                }
            }
        }
        return arraylist
    }
    //アプリネーム取得関数
    fun displayName(appInfo: ApplicationInfo) : CharSequence = this.packageManager.getApplicationLabel(
        appInfo
    )
}

