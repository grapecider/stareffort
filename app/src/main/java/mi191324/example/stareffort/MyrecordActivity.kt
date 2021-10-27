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
        setContentView(R.layout.overlay)

    }
}