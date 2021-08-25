package mi191324.example.stareffort

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

/*class HomeappActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeapp)

        val adapter = AppAdapter(layoutInflater, AppInfo.create(this)) { view, info ->
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun create(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
            .also { it.addCategory(Intent.CATEGORY_LAUNCHER) }
        return pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .mapNotNull { it.activityInfo }
            .filter { it.packageName != context.packageName }
            .map {
                AppInfo(
                    it.loadIcon(pm) ?: getDefaultIcon(context),
                    it.loadLabel(pm).toString(),
                    ComponentName(it.packageName, it.name)
                )
            }
            .sortedBy { it.label }
            .toList()
    }

    //アプリ情報を格納するデータクラス
    data class AppInfo(
        val icon: Drawable,
        val label: String,
        val componentName: ComponentName
    )
}*/