package mi191324.example.stareffort

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import java.util.*


class overlayservice : Service(){
    private var mTimer: Timer? = null
    var mHandler: Handler = Handler()
    private lateinit var surfaceView: SurfaceView
    var view: View? = null
    var wm: WindowManager? = null

    private fun init() {
        //fill values into map here
    }

    override fun onCreate() {
        Log.i("TestService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val layoutInflater = LayoutInflater.from(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        val wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view = layoutInflater.inflate(R.layout.overlay, null)
        wm.addView(view, params)

        return START_STICKY;
    }

    override fun onDestroy() {
        super.onDestroy()
        wm!!.removeView(view)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("TestService", "onBind")
        return null
    }
}