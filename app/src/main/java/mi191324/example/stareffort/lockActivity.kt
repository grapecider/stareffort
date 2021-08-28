package mi191324.example.stareffort

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.json.JSONArray
import org.json.JSONException


class lockActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        //前の画面に戻る処理
        val returnBtn: Button = findViewById(R.id.returnBtn)
        returnBtn.setOnClickListener {
            finish()
        }

        allappget()
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
        val shardPrefEditor = shardPreferences.edit()
        val edit = shardPreferences.edit()
        val prefapp = shardPreferences.getString("applist", "[]")

        val listView:ListView = findViewById(R.id.list_view)
        var appInfolist: ArrayList<AppInfo> = arrayListOf()
        var applistAdapter : applistAdapter = applistAdapter(this, appInfolist)
        //アプリ一覧取得
        val apps = this.packageManager.getInstalledApplications(0)
        var haterapp:List<ApplicationInfo>
        val large = apps.size
        Log.d("size", large.toString())

        var i:Int = 0


        for (appInfo in apps) {
            val packageName = appInfo.packageName
            //プリインストールされたアプリか判別する。
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != ApplicationInfo.FLAG_SYSTEM) {
                val appname = displayName(apps[i])
                val conect = getLaunchIntent(apps[i])
                val icon = loadAppIcon(apps[i])
                val oneapp = AppInfo(icon, appname, true)
                appInfolist.add(oneapp)
            }
            i += 1
        }
        Log.d("haterapp", appInfolist.toString())
        applistAdapter = applistAdapter(this, appInfolist)
        listView.adapter = applistAdapter

        val jsonArray = JSONArray(appInfolist)
        /*val JSONArray = Json.encodeToString(appInfolist)*/
        edit.putString("applist", jsonArray.toString())
            .apply()
        Log.d("final", jsonArray.toString())

        //確認消して良し
        /*val test:ArrayList<AppInfo> = loadArrayList("applist")
        Log.d("final", test.toString())*/

    }

    private fun loadArrayList(data: String): ArrayList<AppInfo> {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val shardPreferences = this.getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val jsonArray = JSONArray(shardPreferences.getString(data, "[]"))
        Log.d("arrayfunction", jsonArray.toString())
        val arrayList : ArrayList<AppInfo> = ArrayList()

        for (i in 0 until jsonArray.length()) {
            val name:String = jsonArray.getJSONObject(i).getString("name")
        }
        Log.d("function", arrayList.toString())

        return arrayList
    }
}