package mi191324.example.stareffort

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.activity_myrecord.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.HashMap

class studytimeFragment : AppCompatActivity() {

    lateinit var barList: ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData

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

    fun onParallelGetButtonClick(id:String) = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
        val httpurl = "https://asia-northeast1-iconic-exchange-326112.cloudfunctions.net/studytime_allget"
        val res = POST(httpurl, id).await()

        Log.d("responseget", res)
    })

    fun POST(url: String, thisid: String) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            val barChart:BarChart = findViewById(R.id.barChart)
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
                            Log.d("res", res.toString())
                            Log.d("size", (res).toString())
                            runOnUiThread(Runnable() {
                                run() {
                                    //recycle.adapter = statelistAdapter(res!!.response)
                                }
                            })
                            runOnUiThread {
                                barList = ArrayList()
                                //barList.add(BarEntry(1, 5))
                                barList.add(BarEntry(2f, 100f))
                                barList.add(BarEntry(3f, 300f))
                                barList.add(BarEntry(4f, 800f))
                                barList.add(BarEntry(5f, 400f))
                                barList.add(BarEntry(6f, 1000f))
                                barList.add(BarEntry(7f, 800f))
                                barDataSet = BarDataSet(barList, "Population")
                                barData = BarData(barDataSet)
                                barChart.data = barData
                                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
                                barDataSet.valueTextColor = Color.BLACK
                                barDataSet.valueTextSize = 15f
                                //recycle.layoutManager = LinearLayoutManager(this@MainActivity)
                                //recycle.adapter = statelistAdapter(res!!.response)
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