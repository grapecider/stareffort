package mi191324.example.stareffort

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class notificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val btnBack : Button = findViewById(R.id.returnBtn)
        val notifiBtn: ToggleButton = findViewById(R.id.notifiBtn)
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        val btnstate = shardPreferences.getString("btnstate", "0")

        if (btnstate == "1") {
            notifiBtn.isChecked = true
        }

        //通知許可ボタンタップ時の処理
        notifiBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("button", "OK")
                edit.putString("btnstate", "1")
                    .apply()
            } else {
                Log.d("button", "NO")
                edit.putString("btnstate", "0")
                    .apply()
            }
        }
        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }

    }
}