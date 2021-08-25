package mi191324.example.stareffort

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity

class lockActivity : AppCompatActivity(){
    var manager:PackageManager? = null
    var apps:List<AppInfo>? = null
    //val ListView:ListView = findViewById(R.id.list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        //前の画面に戻る処理
        val returnBtn: Button = findViewById(R.id.returnBtn)
        returnBtn.setOnClickListener {
            finish()
        }

        val listView:ListView = findViewById(R.id.list)

        lateinit var mCustomAdapter : applistAdapter
        //アプリ一覧取得
        val apps = this.packageManager.getInstalledApplications(0)
        Log.d("app", apps.toString())

        val appname = displayName(apps[0])
        Log.d("name", appname.toString())

        val conect = getLaunchIntent(apps[0])
        Log.d("conect", conect.toString())

        val icon = loadAppIcon(apps[0])
        Log.d("icon", icon.toString())

        val oneapp = AppInfo(icon, appname, true)
        val applist = arrayListOf(oneapp)

        mCustomAdapter = applistAdapter(this, applist)
        /*ここまでOK*/
        listView.adapter = mCustomAdapter
    }

    /*data class apps(
        var icon: Image,
        var name: Int,
    )*/
    /*private class AppData {
        var label: String? = null
        var icon: Drawable? = null
        var pname: String? = null
    }*/
    /*fun loadapp(){
        val ri:ResolveInfo
        manager = getPackageManager()
        apps = ArrayList()

        val i: Intent = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val availableActivities:List<ResolveInfo> = manager!!.queryIntentActivities(i, 0)
        for (ri in availableActivities) {
            val app: AppInfo = AppInfo(
                icon = ri.loadIcon(manager),
                label = ri.loadLabel(manager),
                name = ri.activityInfo.packageName
            )
            (apps as ArrayList<AppInfo>).add(app)
        }
    }*/



    /*private fun loadListView() {
        val adapter = ArrayAdapter<AppInfo>(this, R.layout.lockapplist, apps) {

            var convert:View?
            fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                convert = convertView
                if (convert == null) {
                    convert = layoutInflater.inflate(R.layout.lockapplist, null)
                }
                val appIcon = convert!!.findViewById(R.id.icon) as ImageView
                appIcon.setImageDrawable(apps?.get(position)?.icon)

                val appname = convert!!.findViewById(R.id.name) as TextView
                appIcon.setImageDrawable(apps?.get(position)?.icon)

                return convert as View
            }

        }
        ListView.setAdapter(adapter)
    }*/

    /*private fun addClickLiatener() {
        list.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val i = manager!!.getLaunchIntentForPackage(apps!![position].label.toString())
                startActivity(i)
            }
    }*/

    fun displayName(appInfo: ApplicationInfo) : CharSequence = this.packageManager.getApplicationLabel(appInfo)
    fun getLaunchIntent(appInfo: ApplicationInfo) : Intent? = this.packageManager.getLaunchIntentForPackage(appInfo.packageName)
    fun loadAppIcon(appInfo: ApplicationInfo) : Drawable = this.packageManager.getApplicationIcon(appInfo)

    data class AppInfo(
        val icon: Drawable,
        val name: CharSequence,
        val judgment: Boolean
    )



}