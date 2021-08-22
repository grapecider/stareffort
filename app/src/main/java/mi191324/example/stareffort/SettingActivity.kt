package mi191324.example.stareffort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val profileBtn:Button = findViewById(R.id.profileBtn)
        val lockBtn:Button = findViewById(R.id.lockBtn)
        val friendBtn:Button = findViewById(R.id.friendBtn)
        val notificationBtn:Button = findViewById(R.id.notificationBtn)

        profileBtn.setOnClickListener{
            val intent = Intent(this, profileActivity::class.java)
            startActivity(intent)
        }
        lockBtn.setOnClickListener{
            val intent = Intent(this, lockActivity::class.java)
            startActivity(intent)
        }
        friendBtn.setOnClickListener{
            val intent = Intent(this, addfriendActivity::class.java)
            startActivity(intent)
        }
        notificationBtn.setOnClickListener{
            val intent = Intent(this, notificationActivity::class.java)
            startActivity(intent)
        }

        /*val listView = findViewById<ListView>(R.id.listView)
        val settinglist: MutableList<MutableMap<String,String>> = mutableListOf()

        var itemlist = mutableMapOf("name" to "プロフィール")
        settinglist.add(itemlist)
        itemlist = mutableMapOf("name" to "通知")
        settinglist.add(itemlist)
        itemlist = mutableMapOf("name" to "友達追加")
        settinglist.add(itemlist)
        itemlist = mutableMapOf("name" to "他アプリロック")
        settinglist.add(itemlist)

        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(this@SettingActivity, settinglist, android.R.layout.simple_list_item_1, from, to)
        listView.adapter = adapter

        listView.onItemClickListener = ListItemClick()*/


        // Adapterに渡す配列を作成
        //val data = arrayOf("プロフィール", "通知", "友達追加", "他アプリロック")
        // adapterを作成
        //val adapter = ArrayAdapter(
        //    this,
        //    android.R.layout.simple_list_item_1,
        //    data
        //)
        // adapterをlistViewに紐付け
        //listView.adapter = adapter

        //listView.setOnItemClickListener(this)

        //buckボタンの取得
        val btnBack: Button = findViewById(R.id.btnBack)
        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }
    }

    /*private inner class ListItemClick: AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(
            parent: AdapterView<*>,
            view: View?,
            position: Int,
            id: Long
        ){
            val item = parent.getItemAtPosition(position) as
                    MutableMap<String,String>
            val itemname = item["name"]

            val intent2 = Intent(this, )
        }
    }*/

}