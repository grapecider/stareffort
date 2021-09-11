package mi191324.example.stareffort

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.gesture.GestureOverlayView
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class Serviceclass : Service() {
    private lateinit var surfaceView: SurfaceView
    var view: View? = null
    var wm: WindowManager? = null

    /*private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Overlay レイヤに表示
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  // フォーカスを奪わない
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
        PixelFormat.TRANSLUCENT
    )*/
    //val windowManager: WindowManager by lazy { applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"

        fun start(context: Context) {
            val intent = Intent(context, Serviceclass::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, Serviceclass::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }

        // To control toggle button in MainActivity. This is not elegant but works.
        var isActive = false
            private set
    }
    private lateinit var overlayView: GestureOverlayView

    private var mTimer: Timer? = null
    var mHandler: Handler = Handler()

    override fun onCreate() {
        Log.i("TestService", "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("TestService", "onStartCommand")
        var i = "out"
        var I = 0
        val layoutInflater = LayoutInflater.from(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view = layoutInflater.inflate(R.layout.overlay, null)
        var appname: String
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                val forapp = printForegroundTask()
                val prefapplist = getprefapps()
                val pkgs = allappget()
                //overlay()
                mHandler.post {
                    Log.d("TestService", "Timer run")
                    Log.d("pkgs", pkgs.toString())
                    for (app in pkgs) {
                        Log.d("for", forapp)
                        if (app == forapp.toString()) {
                            i = "in"
                            Log.d("jug", "in")
                            break
                        } else{
                            Log.d("jug", "out")
                        }
                    }
                    if (i == "in"){
                        if (I == 0) {
                            wm.addView(view, params)
                            I = 1
                        }
                    } else {
                        if (I == 1) {
                            wm.removeView(view)
                            I = 0
                        }
                    }
                    i = "out"
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
    //オーバーレイ表示
    fun overlay(){
        var layoutInflater = LayoutInflater.from(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        var wm = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val view: View = layoutInflater.inflate(R.layout.overlay, null)
        wm.addView(view, params)
    }
}

