package com.example.bonusproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import com.example.bonusproject.viewModel.GetTempViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var loadingIndicator: ProgressBar

    private val viewModel: GetTempViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        setupChart()
        fetchData()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchData()
                handler.postDelayed(this, 10000)
            }
        }, 10000)
    }

    private fun setupChart() {
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        lineChart.axisRight.isEnabled = false

        val yAxis = lineChart.axisLeft
        yAxis.setDrawLabels(false)

        lineChart.xAxis.isEnabled = false
    }



    private fun fetchData() {
        loadingIndicator.visibility = View.VISIBLE

        viewModel.getData { response ->
            loadingIndicator.visibility = View.GONE

            println("Response: $response")

            val frames = response.results.A.frames
            val values = frames.firstOrNull()?.data?.values

            if (values != null && values.size >= 2) {
                val timestamps = values[0] as? List<Long>
                val temperatures = values[1] as? List<Double>

                updateChart(timestamps, temperatures)

                val lastTemperature = temperatures?.lastOrNull()
                displayLastTemperature(lastTemperature)
            } else {
                println("Unexpected response structure: $response")
            }
        }
    }


    private fun updateChart(timestamps: List<Long>?, temperatures: List<Double>?) {
        val entries = mutableListOf<Entry>()
        timestamps?.forEachIndexed { index, timestamp ->
            val temperature = temperatures?.get(index)?.toFloat() ?: 0f
            entries.add(Entry(timestamp.toFloat(), temperature))
        }

        val dataSet = LineDataSet(entries, "Temperature")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun displayLastTemperature(lastTemperature: Double?) {
        val lastTemperatureTextView: TextView = findViewById(R.id.lastTemperatureTextView)
        val formattedTemperature = String.format("%.2f", lastTemperature ?: 0.0)
        lastTemperatureTextView.text = "$formattedTemperature °C"
    }
}
