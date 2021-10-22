package mi191324.example.stareffort

import android.app.Service
import android.content.Context
import android.content.Intent
import android.gesture.GestureOverlayView
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import java.lang.NullPointerException
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("TestService", "onStartCommand")
        val layoutInflater = LayoutInflater.from(this)
        view = layoutInflater.inflate(R.layout.overlay, null)
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    Log.d("TestService", "Timerrunn!!!")
                    Log.d("^_^", "雑魚乙")
                }
                if (mTimer.toString() == "null"){
                    Log.d("うんち", "うん")
                    stopSelf()
                }
            }
        }, 1000, 5000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("stopppp", "stop")
        super.onDestroy()
        mTimer = null
        stopSelf()
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
}