package mi191324.example.stareffort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //１）Viewの取得
        val myrecord: Button = findViewById(R.id.myrecord)

        //２）ボタンを押したら次の画面へ
        myrecord.setOnClickListener {
            val intent = Intent(this, MyrecordActivity::class.java)
            startActivity(intent)

            val friendrecord: Button = findViewById(R.id.friendsrecord)

            //3）ボタンを押したら次の画面へ
            friendrecord.setOnClickListener {
                val intent = Intent(this, FriendrecordActivity::class.java)
                startActivity(intent)

                val setting: Button = findViewById(R.id.setting)

                //4）ボタンを押したら次の画面へ
                setting.setOnClickListener {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }
}