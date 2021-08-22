package mi191324.example.stareffort

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
import androidx.preference.PreferenceManager
import org.w3c.dom.Text

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
                val newname = myedit.getText().toString()
                Toast.makeText(this, "名前を変更しました", Toast.LENGTH_SHORT).show()
                edit.putString("username", newname)
                    .apply()

                Log.d("newname", newname)

                val intent = Intent(this, profileActivity::class.java)
                startActivity(intent)
            })
            dialog.setNegativeButton("キャンセル", null)
            dialog.show()

        }

        idtxt.setText(idset)
        nametxt.setText(username)
    }
}