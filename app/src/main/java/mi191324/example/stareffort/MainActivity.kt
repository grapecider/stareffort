package mi191324.example.stareffort

import android.app.AppOpsManager
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceManager
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_homeapp.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ID送信のための変数
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        var idpush = shardPreferences.getString("idpush", "0")
        val username = shardPreferences.getString("username", "Unknown")

        val idlist = shardPreferences.getString("idlist", "[]")
        Log.d("idlist", idlist)

        //permission許可
        if (isaccessGranted()){
            Log.d("permission", "OK")
        }else{
            Log.d("permission", "NO")
            var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
            intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        //初回起動のための変数
        var preference = getSharedPreferences("Preference Name", MODE_PRIVATE)
        var editor = preference.edit()
        //ID作成のための変数
        //val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        //val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val uuid = shardPreferences.getString("uuid", "null")

        if (preference.getBoolean("Laun", false)==false) {
            //初回起動時の処理
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            Log.d("TAG", "初回起動")
            editor.putBoolean("Laun", true)
            editor.commit()
            //ID作成
            createID()
            finish()
        } else {
            //二回目以降の処理
            Log.d("TAG", "２回以降の起動")
        }
        Log.d("ID", uuid)

        //サーバーにIDと名前を送信
        if (idpush == "2"){
            val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/userlist"
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val requestAdapter = moshi.adapter(userlist::class.java)
            val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
            val pushtext = userlist(id = uuid!!, user = username!!)
            val httpAsync = (httpurl)
                .httpPost()
                .header(header).body(requestAdapter.toJson(pushtext))
                .responseString{request, response, result ->
                    Log.d("hoge", result.toString())
                    when(result){
                        is com.github.kittinunf.result.Result.Failure ->{
                            val ex = result.getException()
                            Log.d("response", ex.toString())
                            val myToast: Toast = Toast.makeText(this, "送信失敗しました", Toast.LENGTH_LONG)
                            myToast.show()
                        }

                        is com.github.kittinunf.result.Result.Success -> {
                            val data = result.get()
                            Log.d("responce", data)
                            edit.putString("idpush", "1")
                                .apply()

                            val idadd:ArrayList<String> = arrayListOf()
                            val gson = Gson()
                            idadd.add(uuid)
                            Log.d("idadd", idadd.toString())
                            edit.putString("idlist", gson.toJson(idadd))
                                .apply()
                            val idlist = shardPreferences.getString("idlist", "[]")
                            val  idpush = shardPreferences.getString("idpush", "0")
                            Log.d("idlist", idlist)
                            Log.d("idpush", idpush)
                        }
                    }
                }
            httpAsync.join()
        }

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
        //バックグラウンド処理開始
        startService(Intent(this@MainActivity, Serviceclass::class.java))

        //edit.putString("idpush", "0")
        //    .apply()
        Log.d("firstpush", idpush + username)
    }

    //IDを作成する関数
    private fun createID(){
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        //val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val edit = shardPreferences.edit()
        var id:String?
        var uuid:String?

        id = UUID.randomUUID().toString()
        uuid = (id.substring(0, 7)) + (id.substring(id.length - 10))
        Log.d("uuid", uuid)
        edit.putString("uuid", uuid)
            .apply()
        val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/userlist"
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val requestAdapter = moshi.adapter(userlist::class.java)
        val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
        val username = shardPreferences.getString("username", "Unknown")
        val pushtext = userlist(id = uuid, user = username!!)
        val httpAsync = (httpurl)
            .httpPost()
            .header(header).body(requestAdapter.toJson(pushtext))
            .responseString{request, response, result ->
                Log.d("hoge", result.toString())
                when(result){
                    is com.github.kittinunf.result.Result.Failure ->{
                        val ex = result.getException()
                        Log.d("response", ex.toString())
                        val myToast: Toast = Toast.makeText(this, "送信失敗しました", Toast.LENGTH_LONG)
                        myToast.show()
                        edit.putString("idpush", "2")
                            .apply()
                    }

                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.d("responce", data)
                        edit.putString("idpush", "1")
                            .apply()

                        val idadd:ArrayList<String> = arrayListOf()
                        val gson = Gson()
                        idadd.add(uuid)
                        Log.d("idadd", idadd.toString())
                        edit.putString("idlist", gson.toJson(idadd))
                            .apply()
                        val idlist = shardPreferences.getString("idlist", "[]")
                        val  idpush = shardPreferences.getString("idpush", "0")
                        Log.d("idlist", idlist)
                        Log.d("idpush", idpush)
                    }
                }
            }
        httpAsync.join()
    }

    //permission許可関数
    private fun isaccessGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            } else {
                TODO("VERSION.SDK_INT < KITKAT")
            }
            var mode = 0
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    data class userlist (
        val id: String,
        val user: String
    )
}