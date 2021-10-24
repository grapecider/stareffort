package mi191324.example.stareffort

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.gesture.GestureOverlayView
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.lang.Math.abs
import java.lang.NullPointerException
import java.lang.Runnable
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.jvm.internal.impl.resolve.constants.NullValue


class Service_friendstate : Service() {
    private lateinit var surfaceView: SurfaceView
    var view: View? = null
    var wm: WindowManager? = null

    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"

        fun start(context: Context) {
            val intent = Intent(context, Service_friendstate::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, Service_friendstate::class.java).apply {
                action = ACTION_HIDE
            }
            context.stopService(intent)
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
        val layoutInflater = LayoutInflater.from(this)
        view = layoutInflater.inflate(R.layout.overlay, null)
        mTimer = Timer(true)
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        var b_time = LocalDateTime.now()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                val btnstate = shardPreferences.getString("btnstate", "0")
                val gettime = shardPreferences.getString("picknumber", "2")
                var timespan = gettime!!.toInt()
                Log.d("span", timespan.toString())
                mHandler.post {
                    Log.d("TestService", "Timerrunn!!!")
                    if (btnstate == "1"){
                        var a_time = LocalDateTime.now()
                        val line = abs(a_time.hour - b_time.hour)
                        Log.d("line", line.toString())
                        if (line >= timespan) {
                            onParallelGetButtonClick()
                            b_time = a_time
                        }
                    }
                }
            }
        }, 1000, 3000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("stopppp", "stop")
        super.onDestroy()
        mTimer!!.cancel()
    }
    override fun stopService(name: Intent?): Boolean {
        Log.d("stop", "stop")
        return super.stopService(name)
    }

    override fun onBind(arg0: Intent): IBinder? {
        Log.i("TestService", "onBind")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
        val shardPreferences = getSharedPreferences("KEY", MODE_PRIVATE)
        val gson = Gson()
        val friendList: java.util.ArrayList<String> = gson.fromJson(
            shardPreferences.getString(
                "idlist",
                "[]"
            ),
            object : TypeToken<List<*>>() {}.type
        )
        Log.d("async前", friendList.toString())
        val httpurl =
            "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/who_studying"
        val http = statelistAsyncTask()
        val res = POST(httpurl, friendList).await()

        Log.d("responseget", res)
    })

    fun POST(url: String, flist: ArrayList<String>) : Deferred<String> = GlobalScope.async(
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
            val pushtext = post(ids = flist)
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
                            respon = data.toString()
                            if (respon != "nulll" && respon != username) {
                                //通知に関する変数
                                val CHANNEL_ID = "channel_id"
                                val channel_name = "channel_name"
                                val channel_description = "channel_description "
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val name = channel_name
                                    val descriptionText = channel_description
                                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                                    val channel =
                                        NotificationChannel(CHANNEL_ID, name, importance).apply {
                                            description = descriptionText
                                        }
                                    /// チャネルを登録
                                    val notificationManager: NotificationManager =
                                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                    notificationManager.createNotificationChannel(channel)
                                }
                                var notificationId = 0
                                val builder =
                                    NotificationCompat.Builder(this@Service_friendstate, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                        .setContentTitle(respon + "さんが勉強しています!!")
                                        .setContentText("一緒に頑張りませんか?")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                with(NotificationManagerCompat.from(this@Service_friendstate)) {
                                    notify(notificationId, builder.build())
                                    notificationId += 1
                                }
                            }
                        }
                    }
                }
            httpAsync.join()

            return@async respon
        })

    data class post(
        val ids: List<String>
    )
}