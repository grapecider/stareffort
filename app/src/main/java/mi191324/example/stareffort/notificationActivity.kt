package mi191324.example.stareffort

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_notification.*


class notificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        stopService(Intent(this@notificationActivity, Serviceclass::class.java))

        val btnBack : Button = findViewById(R.id.returnBtn)
        val notifiBtn: ToggleButton = findViewById(R.id.notifiBtn)
        val picknumber: NumberPicker = findViewById(R.id.numberPicker)
        picknumber.setVisibility(View.INVISIBLE)
        val timetxt:TextView = findViewById(R.id.timetxt)
        val hourtxt:TextView = findViewById(R.id.hourtxt)
        timetxt.setVisibility(View.INVISIBLE)
        hourtxt.setVisibility(View.INVISIBLE)
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        val btnstate = shardPreferences.getString("btnstate", "0")
        val username = shardPreferences.getString("username", "Unknown")
        val pickn = shardPreferences.getString("picknumber", "2")
        //通知
        val CHANNEL_ID = "channel_id"
        val channel_name = "channel_name"
        val channel_description = "channel_description "
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_name
            val descriptionText = channel_description
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            /// チャネルを登録
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        var notificationId = 0
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)    /// 表示されるアイコン
            .setContentTitle(username + "さん")                  /// 通知タイトル
            .setContentText("（Ｕ＾ω＾）わんわんお！")           /// 通知コンテンツ
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (btnstate == "1") {
            notifiBtn.isChecked = true
            picknumber.setVisibility(View.VISIBLE)
            timetxt.setVisibility(View.VISIBLE)
            hourtxt.setVisibility(View.VISIBLE)
        }

        //通知許可ボタンタップ時の処理
        notifiBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("button", "OK")
                picknumber.setVisibility(View.VISIBLE)
                timetxt.setVisibility(View.VISIBLE)
                hourtxt.setVisibility(View.VISIBLE)
                edit.putString("btnstate", "1")
                    .apply()
                with(NotificationManagerCompat.from(this)){
                    notify(notificationId, builder.build())
                    notificationId += 1
                }
                startService(Intent(this@notificationActivity, Service_friendstate::class.java))
            } else {
                Log.d("button", "NO")
                picknumber.setVisibility(View.INVISIBLE)
                timetxt.setVisibility(View.INVISIBLE)
                hourtxt.setVisibility(View.INVISIBLE)
                edit.putString("btnstate", "0")
                    .apply()
                var myServiceBinder: Service_friendstate

                stopService(Intent(this, Service_friendstate::class.java))
            }
        }
        //numberpicker設定
        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        numberPicker.value = pickn!!.toInt()
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("nounumber", oldVal.toString())
            Log.d("nounumber", newVal.toString())
            edit.putString("picknumber", newVal.toString())
                .apply()
        }
        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }

    }
}