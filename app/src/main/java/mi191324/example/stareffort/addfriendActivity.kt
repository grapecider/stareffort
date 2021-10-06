package mi191324.example.stareffort

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.util.ArrayList
import java.util.HashMap

class addfriendActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriend)

        //戻るボタンの処理
        val returnBtn: Button = findViewById(R.id.returnBtn)
        returnBtn.setOnClickListener {
            finish()
        }

        //layoutからの取得
        val editid:TextView = findViewById(R.id.editid)
        val searchBtn:Button = findViewById(R.id.searchBtn)

        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val edit = shardPreferences.edit()
        //val idlist = shardPreferences.getString("idlist", "[]")
        val gson = Gson()
        val NameList: ArrayList<*> = gson.fromJson(
            shardPreferences.getString("idlist", "[]"),
            object : TypeToken<List<*>>() {}.type
        )
        var dialogtitle: String = "s"
        var dialogtext: String = "s"

        /*
        var g_NameList: ArrayList<String> = gson.fromJson(
            shardPreferences.getString("idlist", "[]"),
            object : TypeToken<List<*>>() {}.type
        )
        g_NameList.remove("f25ca00bfcf20bddf")

        edit.putString("idlist", gson.toJson(g_NameList))
            .apply()
        Log.d("jjjkkk", g_NameList.toString())
        */


        //searchBtnの押されたときの処理
        searchBtn.setOnClickListener {
            val gettext = editid.text.toString()
            var i = 0
            var how = "false"
            Log.d("gettext", gettext)
            Log.d("searchBtn", NameList.toString())
            while (i < NameList.size) {
                //なにも入力されていないとき
                if (gettext.length == 0) {
                    how = "bad"
                    break
                }
                //入力されたものがすでに追加されているとき
                else if (NameList.get(i).toString() == gettext) {
                    how = "true"
                    break
                }
                Log.d("listids", NameList.get(i).toString())
                i += 1
            }
            Log.d("How", how)
            if (how == "bad") {
                Toast.makeText(this, "入力されていません", Toast.LENGTH_LONG).show()
            } else if (how == "true") {
                Toast.makeText(this, "すでに追加されています", Toast.LENGTH_LONG).show()
            } else if (how == "false") {
                //val dialog = AlertDialog.Builder(this)
                //.setTitle("フレンド追加しました")
                //.setMessage()
                val httpurl =
                    "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/friendsearch"
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val requestAdapter = moshi.adapter(idsearch::class.java)
                val header: HashMap<String, String> =
                    hashMapOf("Content-Type" to "application/json")
                val pushtext = idsearch(id = gettext)
                val httpAsync = (httpurl)
                    .httpPost()
                    .header(header).body(requestAdapter.toJson(pushtext))
                    .responseString { request, response, result ->
                        Log.d("hoge", result.toString())
                        when (result) {
                            is com.github.kittinunf.result.Result.Failure -> {
                                val ex = result.getException()
                                Log.d("response", ex.toString())
                                //Toast.makeText(applicationContext, "送信失敗しました", Toast.LENGTH_LONG).show()
                                dialogtitle = "検索ができませんでした"
                                dialogtext = "ネット状況を見直してみてください"
                            }

                            is com.github.kittinunf.result.Result.Success -> {
                                val data = result.get()
                                Log.d("result", data)
                                //Toast.makeText(applicationContext, "送信成功しました", Toast.LENGTH_LONG).show()
                                if (data == "Null") {
                                    dialogtitle = "フレンドが見つかりませんでした"
                                    dialogtext = "検索したIDを見直してください"
                                } else {
                                    dialogtitle = "フレンドが見つかりました"
                                    dialogtext = data + "さんがフレンドリストに追加されました"
                                    var n_NameList: ArrayList<String> = gson.fromJson(
                                        shardPreferences.getString("idlist", "[]"),
                                        object : TypeToken<List<*>>() {}.type
                                    )
                                    Log.d("old_list", n_NameList.toString())
                                    n_NameList.add(gettext)
                                    edit.putString("idlist", gson.toJson(n_NameList))
                                        .apply()
                                    var o_NameList: ArrayList<String> = gson.fromJson(
                                        shardPreferences.getString("idlist", "[]"),
                                        object : TypeToken<List<*>>() {}.type
                                    )
                                    Log.d("new_list", o_NameList.toString())
                                }
                            }
                        }
                    }
                httpAsync.join()

                //Toast.makeText(this, "検索します", Toast.LENGTH_LONG).show()
                val dialog = AlertDialog.Builder(this)
                    .setTitle(dialogtitle!!) // タイトル
                    .setMessage(dialogtext!!) // メッセージ
                    .setPositiveButton("OK") { dialog, which -> // OK
                        //Toast.makeText(applicationContext,"OKがタップされた", Toast.LENGTH_SHORT).show()
                        if (dialogtitle == "フレンドが見つかりました"){
                            finish()
                            val intent = Intent(this, addfriendActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    .create()
                // AlertDialogを表示
                dialog.show()
                Log.d("dialogtitle", dialogtitle)
            }
        }
    }

    data class idsearch (
        val id: String
    )
}