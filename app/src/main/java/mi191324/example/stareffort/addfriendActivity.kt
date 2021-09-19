package mi191324.example.stareffort

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class addfriendActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriend)

        val returnBtn: Button = findViewById(R.id.returnBtn)

        returnBtn.setOnClickListener {
            finish()
        }

    }
}