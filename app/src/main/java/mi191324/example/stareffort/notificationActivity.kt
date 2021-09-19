package mi191324.example.stareffort

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class notificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val btnBack : Button = findViewById(R.id.returnBtn)

        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }

    }
}