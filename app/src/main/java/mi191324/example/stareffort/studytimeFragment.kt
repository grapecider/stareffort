package mi191324.example.stareffort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_homeapp.view.*
import kotlinx.android.synthetic.main.activity_myrecord.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList


class studytimeFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studyfragmentlayout)

        val returnBtn: Button = findViewById(R.id.returnBtn)
        val calenderBtn: Button = findViewById(R.id.calenderBtn)
        //val shardPreferences = getSharedPreferences("KEY", Context.MODE_PRIVATE)
        //val edit = shardPreferences.edit()
        val thisid = intent.getStringExtra("id")
        Log.d("thisid", thisid)
        onParallelGetButtonClick(thisid)
        //カレンダー画面へ移動する
        calenderBtn.setOnClickListener{
            val intent = Intent(this, calenderActivity::class.java)
            intent.putExtra("id", thisid)
            startActivity(intent)
            finish()
        }
        //元の画面に戻る
        returnBtn.setOnClickListener {
            finish()
        }

    }

    fun onParallelGetButtonClick(id: String) = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.DEFAULT,
        {
            val httpurl =
                "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/studytime_allget"
            val res = POST(httpurl, id).await()

            Log.d("responseget", res)
        })

    fun POST(url: String, thisid: String) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            val entries = ArrayList<BarEntry>()
            val labels: ArrayList<String> = ArrayList()
            val barChart: BarChart = findViewById(R.id.barChart)
            var respon = "null"
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val requestAdapter = moshi.adapter(sendid::class.java)
            val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
            val pushtext = sendid(id = thisid)
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
                            val res = moshi.adapter(responcelist::class.java).fromJson(data)
                            var count = 0.01
                            for (i in res!!.response) {
                                labels.add(i.year.toString() + "/" + i.month.toString() + "/" + i.day.toString())
                                entries.add(BarEntry(count.toFloat(), i.time.toFloat()))
                                count++
                            }
                            runOnUiThread(Runnable() {
                                run() {
                                }
                            })
                            runOnUiThread {
                                Log.d("x", labels.toString())

                                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                barChart.setDrawGridBackground(false)
                                barChart.axisLeft.isEnabled = false
                                barChart.axisRight.isEnabled = false
                                barChart.description.isEnabled = false
                                val set = BarDataSet(entries, "")
                                set.valueTextSize = 10f

                                barChart.data = BarData(set)
                                barChart.invalidate()

                            }
                        }
                    }
                }
            httpAsync.join()

            return@async respon
        })

    data class sendid(
        val id: String
    )
    data class getdata(
        val day: Int,
        val month: Int,
        val time: Int,
        val year: Int
    )
    data class responcelist(
        val response: List<getdata>
    )
}