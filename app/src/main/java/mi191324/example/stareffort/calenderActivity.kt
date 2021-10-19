package mi191324.example.stareffort

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*

class calenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        val returnBtn: Button = findViewById(R.id.returnBtn)
        val calender: CalendarView = findViewById(R.id.calendar)
        val thisid = intent.getStringExtra("id")
        val today = Date()
        val format_y = SimpleDateFormat("yyyy", Locale.getDefault())
        val format_m = SimpleDateFormat("M", Locale.getDefault())
        val format_D = SimpleDateFormat("d", Locale.getDefault())
        val year = (format_y.format(today)).toInt()
        val month = (format_m.format(today)).toInt()
        val day = (format_D.format(today)).toInt()
        Log.d("year", year.toString())
        Log.d("month", month.toString())
        Log.d("day", day.toString())
        onParallelGetButtonClick(thisid, year, month, day)

        //元の画面に戻る
        returnBtn.setOnClickListener {
            val intent = Intent(this, studytimeFragment::class.java)
            intent.putExtra("id", thisid)
            finish()
            startActivity(intent)
        }

        //カレンダータップ時の処理
        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            //val date = "$year/$month/$dayOfMonth"
            Log.d("year_t", year.toString())
            Log.d("month_t", (month + 1).toString())
            Log.d("day_t", dayOfMonth.toString())
            onParallelGetButtonClick(thisid, year, month + 1, dayOfMonth)
        }
    }

    fun post(url: String, thisid: String, year: Int, month: Int, day: Int) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default, CoroutineStart.DEFAULT) {
        var respon = "null"
        val timetext: TextView = findViewById(R.id.timetxt)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val requestAdapter = moshi.adapter(allday_send::class.java)
        val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
        val pushtext = allday_send(userlist = thisid, year = year, month = month, day = day)
        Log.d("sendlist", pushtext.toString())
        val httpAsync = url
            .httpPost()
            .header(header).body(requestAdapter.toJson(pushtext))
            .responseString() { request, response, result ->
                Log.d("hoge", result.toString())
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.d("response", ex.toString())
                    }
                    is Result.Success -> {
                        val data = result.get()
                        runOnUiThread(Runnable() {
                            run() {
                                //recycle.adapter = statelistAdapter(res!!.response)
                            }
                        })
                        runOnUiThread {
                            val settext = data + "分"
                            Log.d("settext", settext)
                            timetext.setText(settext)
                        }
                    }
                }
            }
        httpAsync.join()

        return@async respon
    }

    //今日の勉強時間取得
        fun onParallelGetButtonClick(thisid:String, year: Int, month: Int, day: Int) = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
            val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/studytime_get"
            val res = post(httpurl, thisid, year, month, day)
            Log.d("responseget", res.toString())
        })

    data class allday_send(
        val userlist: String,
        val year: Int,
        val month: Int,
        val day: Int
    )
}