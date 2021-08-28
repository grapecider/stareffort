package mi191324.example.stareffort

import android.Manifest
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
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初回起動のための変数
        var preference = getSharedPreferences("Preference Name", MODE_PRIVATE)
        var editor = preference.edit()
        //ID作成のための変数
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val uuid = pref.getString("uuid", "null")

        if (preference.getBoolean("Launched", false)==false) {
            //初回起動時の処理
            Log.d("TAG", "初回起動")
            editor.putBoolean("Launched", true)
            editor.commit()
            //ID作成
            createID()
        } else {
            //二回目以降の処理
            Log.d("TAG", "２回以降の起動")
        }
        Log.d("ID", uuid)

        val myrecord: Button = findViewById(R.id.myrecord)
        val friendrecord: Button = findViewById(R.id.friendsrecord)
        val setting: Button = findViewById(R.id.setting)

        //myrecordボタンを押したらMyrecordActivityへ
        myrecord.setOnClickListener {
            val intent = Intent(this, MyrecordActivity::class.java)
            startActivity(intent)
        }
        //friendrecordボタンを押したらFriendrecordActivityへ
        friendrecord.setOnClickListener {
            val intent = Intent(this, FriendrecordActivity::class.java)
            startActivity(intent)
        }
        //4）settingボタンを押したらSettingActivityへ
        setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createID(){ //IDを作成する関数
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val edit = pref.edit()
        var id:String? = null
        var uuid:String? = null

        id = UUID.randomUUID().toString()
        uuid = (id.substring(0, 7)) + (id.substring(id.length - 10))
        edit.putString("uuid", uuid)
            .apply()
    }
}

