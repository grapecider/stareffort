package mi191324.example.stareffort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MyrecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myrecord)
        val btnBack :Button = findViewById(R.id.btnBack)

        //３）戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }
    }
}