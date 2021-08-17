package mi191324.example.stareffort

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AppLaunchChecker
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.UUID
import android.content.Intent
import android.widget.Button


class MainActivity : AppCompatActivity() {
    //val pref = PreferenceManager.getDefaultSharedPreferences(this)
    //val uuid = pref.getString("uuid", "null")
    //val edit = pref.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初回起動のための値
        var preference = getSharedPreferences("Preference Name", MODE_PRIVATE);
        var editor = preference.edit();

        if (preference.getBoolean("Launched", false)==false) {
            //初回起動時の処理
            Log.d("TAG", "初回起動")
            editor.putBoolean("Launched", true)
            editor.commit()
            //ID作成
            //edit.putString("uuid", UUID.randomUUID().toString())
                //.apply()
            //Log.d("ID", uuid)
        } else {
            //二回目以降の処理
            Log.d("TAG", "２回以降の起動")
            //IDが作成されていなかった場合のID作成
            //if (uuid == "null") {
            //edit.putString("uuid", UUID.randomUUID().toString())
            //.apply()
            //}
            //Log.d("ID", uuid)
            //１）Viewの取得
        }
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

