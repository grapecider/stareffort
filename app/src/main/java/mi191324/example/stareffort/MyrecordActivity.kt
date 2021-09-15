package mi191324.example.stareffort

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.activity_myrecord.*

class MyrecordActivity : AppCompatActivity() {
    lateinit var barList: ArrayList<BarEntry>

    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myrecord)

        val btnBack :Button = findViewById(R.id.btnBack)

        //戻るボタン（アクティビティの終了）
        btnBack.setOnClickListener {
            finish()
        }
        barList = ArrayList()
        barList.add(BarEntry(1f, 500f))
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

    }
}