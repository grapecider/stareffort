package mi191324.example.stareffort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Adapterに渡す配列を作成
        val data = arrayOf("プロフィール", "通知", "友達追加", "他アプリロック")

        // adapterを作成
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )

        // adapterをlistViewに紐付け
        listView.adapter = adapter


val btnBack: Button = findViewById(R.id.btnBack)

            //戻るボタン（アクティビティの終了）
            btnBack.setOnClickListener {
                finish()
            }
        }
    }