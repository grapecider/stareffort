package mi191324.example.stareffort

import android.Manifest
import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.provider.Settings.canDrawOverlays
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_homeapp.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*


class MainActivity : AppCompatActivity() {
    val REQUEST_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ID送信のための変数
        val shardPreferences = getSharedPreferences("KEY", MODE_PRIVATE)
        val edit = shardPreferences.edit()
        var idpush = shardPreferences.getString("idpush", "0")
        val username = shardPreferences.getString("username", "Unknown")
        val recycle:RecyclerView = findViewById(R.id.recycle)
        val friendrecord: Button = findViewById(R.id.friendsrecord)
        val setting: Button = findViewById(R.id.setting)

        val idlist = shardPreferences.getString("idlist", "[]")
        Log.d("idlist", idlist)

        //permission許可
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PACKAGE_USAGE_STATS", "OK")
            } else {
                Log.d("PACKAGE_USAGE_STATS", "NO")
                showAlertDialog(supportFragmentManager, Manifest.permission.PACKAGE_USAGE_STATS)
            }
            if (checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
                Log.d("SYSTEM_ALERT_WINDOW", "OK")
            } else {
                Log.d("SYSTEM_ALERT_WINDOW", "NO")
                showAlertDialog(supportFragmentManager, Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
        }

        //初回起動のための変数
        var preference = getSharedPreferences("Preference Name", MODE_PRIVATE)
        var editor = preference.edit()
        //ID作成のための変数
        val uuid = shardPreferences.getString("uuid", "null")

        if (preference.getBoolean("Laun", false)==false) {
            //初回起動時の処理
            //val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            //startActivity(intent)
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
                .responseString{ request, response, result ->
                    Log.d("hoge", result.toString())
                    when(result){
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("response", ex.toString())
                            val myToast: Toast = Toast.makeText(this, "送信失敗しました", Toast.LENGTH_LONG)
                            myToast.show()
                        }

                        is Result.Success -> {
                            val data = result.get()
                            Log.d("responce", data)
                            edit.putString("idpush", "1")
                                .apply()

                            val idadd: ArrayList<String> = arrayListOf()
                            val gson = Gson()
                            idadd.add(uuid)
                            Log.d("idadd", idadd.toString())
                            edit.putString("idlist", gson.toJson(idadd))
                                .apply()
                            val idlist = shardPreferences.getString("idlist", "[]")
                            val idpush = shardPreferences.getString("idpush", "0")
                            Log.d("idlist", idlist)
                            Log.d("idpush", idpush)
                        }
                    }
                }
            httpAsync.join()
        }

        //friendrecordボタンを押したら友達の状態再取得
        friendrecord.setOnClickListener {
            onParallelGetButtonClick()
        }
        //4）settingボタンを押したら設定画面(SettingActivity)へ
        setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        //バックグラウンド処理開始
        startService(Intent(this@MainActivity, Serviceclass::class.java))

        val notifystate = shardPreferences.getString("btnstate", "0")
        Log.d("noti", notifystate)
        if (notifystate == "1"){
            startService(Intent(this@MainActivity, Service_friendstate::class.java))
        }

        onParallelGetButtonClick()


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
            .responseString{ request, response, result ->
                Log.d("hoge", result.toString())
                when(result){
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.d("response", ex.toString())
                        val myToast: Toast = Toast.makeText(this, "送信失敗しました", Toast.LENGTH_LONG)
                        myToast.show()
                        edit.putString("idpush", "2")
                            .apply()
                    }

                    is Result.Success -> {
                        val data = result.get()
                        Log.d("responce", data)
                        edit.putString("idpush", "1")
                            .apply()

                        val idadd: ArrayList<String> = arrayListOf()
                        val gson = Gson()
                        idadd.add(uuid)
                        Log.d("idadd", idadd.toString())
                        edit.putString("idlist", gson.toJson(idadd))
                            .apply()
                        val idlist = shardPreferences.getString("idlist", "[]")
                        val idpush = shardPreferences.getString("idpush", "0")
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

    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
        val shardPreferences = getSharedPreferences("KEY", MODE_PRIVATE)
        val gson = Gson()
        val friendList: java.util.ArrayList<String> = gson.fromJson(
            shardPreferences.getString(
                "idlist",
                "[]"
            ),
            object : TypeToken<List<*>>() {}.type
        )
        Log.d("async前", friendList.toString())
        val httpurl =
            "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/friendstate_get"
        val http = statelistAsyncTask()
        val res = POST(httpurl, friendList).await()

        Log.d("responseget", res)
    })

    fun POST(url: String, flist: ArrayList<String>) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            //val handler = Handler()

            val recycle: RecyclerView = findViewById(R.id.recycle)
            var respon = "null"
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val requestAdapter = moshi.adapter(MainActivity.ids::class.java)
            val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
            val pushtext = MainActivity.ids(ids = flist)
            val httpAsync = url
                .httpPost()
                .header(header).body(requestAdapter.toJson(pushtext))
                .responseString() { request, response, result ->
                    Log.d("hoge", result.toString())
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("response", ex.toString())
                        }
                        is Result.Success -> {
                            val data = result.get()
                            val res =
                                moshi.adapter(MainActivity.statelistresponce::class.java).fromJson(
                                    data
                                )
                            Log.d("res", res.toString())
                            //recycleview(res)
                            respon = res.toString()
                            runOnUiThread(Runnable() {
                                run() {
                                    //recycle.adapter = statelistAdapter(res!!.response)
                                }
                            })
                            runOnUiThread {
                                recycle.layoutManager = LinearLayoutManager(this@MainActivity)
                                recycle.adapter = statelistAdapter(res!!.response)
                            }
                        }
                    }
                }
            httpAsync.join()

            return@async respon
        })


    //ssss
    fun showAlertDialog(fragmentManager: FragmentManager, permission: String) {
        val dialog = RuntimePermissionAlertDialogFragment.newInstance(permission)
        dialog.show(fragmentManager, RuntimePermissionAlertDialogFragment.TAG)
    }

    // ダイアログ本体
    class RuntimePermissionAlertDialogFragment : DialogFragment() {

        companion object {
            const val TAG = "RuntimePermissionApplicationSettingsDialogFragment"
            private const val ARG_PERMISSION_NAME = "permissionName"
            fun newInstance(permission: String): RuntimePermissionAlertDialogFragment {
                val fragment = RuntimePermissionAlertDialogFragment()
                val args = Bundle()
                args.putString(ARG_PERMISSION_NAME, permission)
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val permission = arguments?.getString(ARG_PERMISSION_NAME)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setMessage(permission!! + "の権限がないので、アプリ情報の「許可」から設定してください")
                .setPositiveButton("アプリ情報", DialogInterface.OnClickListener { _, _ ->
                    dismiss()
                    // システムのアプリ設定画面
                    val intent = Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity?.packageName)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                })
                .setNegativeButton("キャンセル", DialogInterface.OnClickListener { _, _ -> dismiss() })
            return dialogBuilder.create()
        }
    }

    data class userlist(
        val id: String,
        val user: String
    )

    //フレンド状態一覧取得用のデータリスト
    data class ids(
        val ids: List<String>
    )

    data class statelist(
        val number: Int,
        val id: String,
        val user: String,
        val state: Int
    )
    data class statelistresponce(
        val response: List<statelist>
    )

}