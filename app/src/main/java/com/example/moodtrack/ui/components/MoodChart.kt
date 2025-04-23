package com.example.moodtrack.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moodtrack.data.local.entity.MoodEntity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun MoodChart(moodList: List<MoodEntity>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.text = "Mood Tracker"
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                setPinchZoom(true)

                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }
        },
        update = { chart ->
            val entries = moodList.mapIndexed { index, mood ->
                Entry(index.toFloat(), mood.mood.toFloat())
            }

            val dataSet = LineDataSet(entries, "Mood Level").apply {
                color = ColorTemplate.COLORFUL_COLORS[0]
                valueTextColor = Color.BLACK
                setDrawCircles(true)
                setDrawValues(true)

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}