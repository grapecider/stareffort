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
import android.view.*
import androidx.annotation.RequiresApi
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*


class Serviceclass : Service() {
    private lateinit var surfaceView: SurfaceView
    var view: View? = null
    var wm: WindowManager? = null
    var onlinestate:Int = 0

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("TestService", "onStartCommand")
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        var b_time = LocalDateTime.now()
        var i = "out"
        var I = 0
        //
        val typeLayer = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        var windowManager = applicationContext
            .getSystemService(WINDOW_SERVICE) as WindowManager
        val paramss = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            typeLayer, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        //
        val layoutInflater = LayoutInflater.from(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        val wm = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val nullParent: ViewGroup? = null
        view = layoutInflater.inflate(R.layout.overlay, nullParent)
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                val forapp = printForegroundTask()
                val pkgs = allappget()
                mHandler.post {
                    Log.d("pkgs", pkgs.toString())
                    onParallelGetButtonClick()
                    Log.d("state", onlinestate.toString())
                    if (onlinestate == 1) {
                        for (app in pkgs) {
                            if (app == forapp.toString()) {
                                i = "in"
                                break
                            }
                        }
                    }
                    if (i == "in") {
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
        }, 10000, 10000)
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
        var i = 1
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

    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
        val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/online_you"
        val res = POST(httpurl).await()

        Log.d("responseget", res)
    })

    fun POST(url: String) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
            val username = shardPreferences.getString("username", "Unknown")
            val myid = shardPreferences.getString("uuid", "Unknown")
            var respon = "null"
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val requestAdapter = moshi.adapter(post::class.java)
            val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
            val pushtext = post(id = myid!!)
            val httpAsync = url
                .httpPost()
                .header(header).body(requestAdapter.toJson(pushtext))
                .responseString() { request, response, result ->
                    Log.d("hoge", result.toString())
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("response", ex.toString())
                        }
                        is Result.Success -> {
                            val data = result.get()
                            Log.d("res", data.toString())
                            onlinestate = data.toInt()
                        }
                    }
                }
            httpAsync.join()

            return@async respon
        })

    data class post(
        val id: String
    )
}

