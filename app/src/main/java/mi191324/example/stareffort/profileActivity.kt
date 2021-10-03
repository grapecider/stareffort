package mi191324.example.stareffort

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceManager
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.w3c.dom.Text
import java.util.HashMap

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
        val edit = pref.edit()
        val myid = pref.getString("uuid", "Unknown")
        val username = pref.getString("username", "Unknown")
        val idset = "ID:" + myid

        changeBtn.setOnClickListener{
            val myedit = EditText(this)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("新しい文字を入力してください")
            dialog.setView(myedit)
            dialog.setPositiveButton("OK", DialogInterface.OnClickListener {_, _ ->
                val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/name_change"
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val requestAdapter = moshi.adapter(userlist::class.java)
                val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
                var newname:String = myedit.getText().toString()
                if (newname.length == 0){
                    newname = username!!
                }
                val pushtext = userlist(id = myid!!, name = newname)
                val httpAsync = (httpurl)
                    .httpPost()
                    .header(header).body(requestAdapter.toJson(pushtext))
                    .responseString { request, response, result ->
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
                                Toast.makeText(this, "名前を変更しました", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                edit.putString("username", newname)
                    .apply()
                Log.d("newname", newname)
                finish()
                val intent = Intent(this, profileActivity::class.java)
                startActivity(intent)
            })
            dialog.setNegativeButton("キャンセル", null)
            dialog.show()

        }

        idtxt.setText(idset)
        nametxt.setText(username)
    }

    data class userlist (
        val id: String,
        val name: String
    )
}