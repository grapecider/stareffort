package mi191324.example.stareffort

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.util.*

class profileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //元の画面に戻る
        val returnBtn:Button = findViewById(R.id.returnBtn)
        returnBtn.setOnClickListener {
            finish()
        }

        val idtxt:TextView = findViewById(R.id.idtxt)
        val nametxt:TextView = findViewById(R.id.nametxt)
        val changeBtn:Button = findViewById(R.id.changeBtn)
        val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val edit = shardPreferences.edit()
        val myid = shardPreferences.getString("uuid", "Unknown")
        val username = shardPreferences.getString("username", "Unknown")
        val idset = "ID:" + myid

        changeBtn.setOnClickListener{
            val myedit = EditText(this)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("新しい文字を入力してください")
            dialog.setView(myedit)
            dialog.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val httpurl =
                    "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/name_change"
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val requestAdapter = moshi.adapter(userlist::class.java)
                val header: HashMap<String, String> =
                    hashMapOf("Content-Type" to "application/json")
                var newname: String = myedit.getText().toString()
                if (newname.length == 0) {
                    newname = username!!
                }
                val pushtext = userlist(id = myid!!, name = newname)
                val httpAsync = (httpurl)
                    .httpPost()
                    .header(header).body(requestAdapter.toJson(pushtext))
                    .responseString { request, response, result ->
                        Log.d("hoge", result.toString())
                        when (result) {
                            is Result.Failure -> {
                                val ex = result.getException()
                                Log.d("response", ex.toString())
                                //Toast.makeText(this, "名前変更失敗しました", Toast.LENGTH_LONG).show()
                                edit.putString("last", "2")
                                    .apply()
                            }

                            is Result.Success -> {
                                val data = result.get()
                                Log.d("responce", data)
                                edit.putString("username", newname)
                                    .apply()
                                //Toast.makeText(this, "名前変更しました", Toast.LENGTH_LONG).show()
                                edit.putString("last", "1")
                                    .apply()
                            }
                        }
                    }
                httpAsync.join()
                finish()
                val intent = Intent(this, profileActivity::class.java)
                startActivity(intent)
            })
            dialog.setNegativeButton("キャンセル", null)
            dialog.show()
        }
        idtxt.setPaintFlags(idtxt.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        idtxt.setText(idset)
        nametxt.setPaintFlags(nametxt.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        nametxt.setText("名前:" + username)
        val last = shardPreferences.getString("last", "0")
        if (last == "1"){
            Toast.makeText(this, "名前変更しました", Toast.LENGTH_LONG).show()
            edit.putString("last", "0")
                .apply()
            Log.d("last", "1")
        }
        else if (last == "2"){
            Toast.makeText(this, "名前変更失敗しました", Toast.LENGTH_LONG).show()
            edit.putString("last", "0")
                .apply()
            Log.d("last", "2")
        }
        else {
            Log.d("last", "0")
        }
    }

    data class userlist(
        val id: String,
        val name: String
    )
}