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
import android.widget.Toast
import java.util.*


class Service_friendstate : Service() {
    private lateinit var surfaceView: SurfaceView
    var view: View? = null
    var wm: WindowManager? = null

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
        val layoutInflater = LayoutInflater.from(this)
        //val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view = layoutInflater.inflate(R.layout.overlay, null)
        mTimer = Timer(true)
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    Log.d("TestService", "Timer run")
                    Log.d("^_^", "雑魚乙")
                }
            }
        }, 1000, 10000)
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i("TestService", "onDestroy")
    }

    override fun onBind(arg0: Intent): IBinder? {
        Log.i("TestService", "onBind")
        return null
    }
}