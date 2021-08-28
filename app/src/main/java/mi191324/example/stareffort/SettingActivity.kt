package mi191324.example.stareffort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val profileBtn:Button = findViewById(R.id.profileBtn)
        val lockBtn:Button = findViewById(R.id.lockBtn)
        val friendBtn:Button = findViewById(R.id.friendBtn)
        val notificationBtn:Button = findViewById(R.id.notificationBtn)

        profileBtn.setOnClickListener{
            val intent = Intent(this, profileActivity::class.java)
            startActivity(intent)
        }
        lockBtn.setOnClickListener{
            val intent = Intent(this, lockActivity::class.java)
            startActivity(intent)
        }
        friendBtn.setOnClickListener{
            val intent = Intent(this, addfriendActivity::class.java)
            startActivity(intent)
        }
        notificationBtn.setOnClickListener{
            val intent = Intent(this, notificationActivity::class.java)
            startActivity(intent)
        }

        //buckボタンの取得
        val btnBack: Button = findViewById(R.id.btnBack)
        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }
    }

}