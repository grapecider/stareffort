package mi191324.example.stareffort

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class applistAdapter(context: Context, var applist: List<lockActivity.AppInfo>) : ArrayAdapter<lockActivity.AppInfo>(context, 0, applist) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun View?.getView(position: Int, parent: ViewGroup?): View {
        // Animalの取得
        val animal = applist[position]

        // レイアウトの設定
        var view = this
        if (this == null) {
            view = layoutInflater.inflate(R.layout.lockapplist, parent, false)
        }

        // 各Viewの設定
        val imageView = view?.findViewById<ImageView>(R.id.icon)
        imageView?.setImageDrawable(animal.icon)

        val name = view?.findViewById<TextView>(R.id.nametxt)
        name?.text = animal.name

        val age = view?.findViewById<TextView>(R.id.judgmenttxt)
        age?.text = "judgment"

        return view!!
    }
}