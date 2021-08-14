package mi191324.example.stareffort

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AppLaunchChecker
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var preference = getSharedPreferences("Preference Name", MODE_PRIVATE);
        var editor = preference.edit();

        if (preference.getBoolean("Launched", false)==false) {
            //初回起動時の処理
            Log.d("TAG", "初回起動")
            //プリファレンスの書き変え
            editor.putBoolean("Launched", true);
            editor.commit();
        } else {
            //二回目以降の処理
            Log.d("TAG", "２回以降の起動")
        }
    }
}

