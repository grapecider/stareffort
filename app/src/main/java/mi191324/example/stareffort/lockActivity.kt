package mi191324.example.stareffort

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class lockActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        //前の画面に戻る処理
        val listView:ListView = findViewById(R.id.list_view)
        val returnBtn: Button = findViewById(R.id.returnBtn)
        returnBtn.setOnClickListener {
            finish()
        }
        //ListViewにアプリ一覧表示
        allappget()
        //listタップ後の処理
        listView.setOnItemClickListener { parent, view, position, id ->
            val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
            val edit = shardPreferences.edit()
            val gson = Gson()
            Log.d("tap", position.toString())
            var index = position * 2
            var saveapp:ArrayList<String> = getprefapps() as ArrayList<String>
            Log.d("tapname", saveapp.get(index))
            index++
            if (saveapp.get(index) == "true"){
                saveapp.set(index, "false")
            } else {
                saveapp.set(index, "true")
            }
            edit.putString("applist", gson.toJson(saveapp))
                .apply()
            val newapplist = shardPreferences.getString("applist", "[]")
            Log.d("tap_3", newapplist.toString())
            allappget()
        }
    }

    fun displayName(appInfo: ApplicationInfo) : CharSequence = this.packageManager.getApplicationLabel(
        appInfo
    )
    fun getLaunchIntent(appInfo: ApplicationInfo) : Intent? = this.packageManager.getLaunchIntentForPackage(
        appInfo.packageName
    )
    fun loadAppIcon(appInfo: ApplicationInfo) : Drawable = this.packageManager.getApplicationIcon(
        appInfo
    )

    //アプリ取得の際に使うデータリスト
    data class AppInfo(
        val icon: Drawable,
        val name: CharSequence,
        val judgment: Boolean
    )

    //アプリ一覧取得関数
    fun allappget(){
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        val prefapp = shardPreferences.getString("applist", "[]")
        val listView:ListView = findViewById(R.id.list_view)
        var appInfolist: ArrayList<AppInfo> = arrayListOf()
        var applistAdapter : applistAdapter = applistAdapter(this, appInfolist)
        val gson = Gson()
        //アプリ一覧取得
        val allapps = this.packageManager.getInstalledApplications(0)
        var saveapp:ArrayList<String> = arrayListOf() //保存用の変数
        val NameList:ArrayList<String> = getprefapps() as ArrayList<String>
        Log.d("list", NameList.toString())
        var i = 0
        var I = 0
        for (appInfo in allapps) {
            val packageName = appInfo.packageName
            //プリインストールされたアプリか判別する。
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != ApplicationInfo.FLAG_SYSTEM) {
                val appname = displayName(allapps[i])
                val conect = getLaunchIntent(allapps[i])
                val icon = loadAppIcon(allapps[i])
                var judgment:Boolean = true
                while (I < NameList.size) {
                    if (appname.toString() == NameList.get(I)) {
                        I++
                        Log.d("login", NameList.get(I))
                        if (NameList.get(I) == "true"){
                            judgment = true
                        }else {
                            judgment = false
                        }
                    }
                    I = I + 2
                }
                val oneapp = AppInfo(icon, appname, judgment)
                appInfolist.add(oneapp)
                saveapp.add(appname.toString())
                saveapp.add(judgment.toString())
            }
            I = 0
            i += 1
        }
        applistAdapter = applistAdapter(this, appInfolist)
        listView.adapter = applistAdapter

        edit.putString("applist", gson.toJson(saveapp))
            .apply()

        val name = NameList.get(0)
    }
    //保存したArrayListを呼び出す関数
    fun getprefapps(): ArrayList<*> {
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val gson = Gson()
        val NameList: ArrayList<*> = gson.fromJson(
            shardPreferences.getString("applist", null),
            object : TypeToken<List<*>>() {}.type
        )
        return NameList
    }
}