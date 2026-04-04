package com.example.consego.ui.features.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarGraphWithAxes(data: Map<String, Double>) {
    val maxVal = (data.values.maxOrNull() ?: 1.0).toFloat()
    val yLabels = listOf(maxVal, maxVal * 0.66f, maxVal * 0.33f, 0f)

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Column(
                modifier = Modifier.fillMaxHeight().padding(bottom = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yLabels.forEach { label ->
                    Text("$${label.toInt()}", fontSize = 10.sp, color = Color.LightGray)
                }
            }

            Canvas(modifier = Modifier.fillMaxSize().padding(start = 45.dp, bottom = 20.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barCount = data.size
                val barWidth = canvasWidth / (barCount * 2f)
                val spacing = barWidth

                yLabels.forEach { label ->
                    val yPos = canvasHeight - (label / maxVal) * canvasHeight
                    drawLine(Color(0xFFF1F1F1), Offset(0f, yPos), Offset(canvasWidth, yPos), 1f)
                }

                data.values.forEachIndexed { index, value ->
                    val barHeight = (value.toFloat() / maxVal) * canvasHeight
                    val xOffset = spacing / 2 + (index * (barWidth + spacing))
                    drawRoundRect(
                        color = Color(0xFF744BD7),
                        topLeft = Offset(xOffset, canvasHeight - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 45.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.keys.forEach { month ->
                Text(text = month, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EnhancedPieChart(data: Map<String, Double>, total: Double) {
    val colors = listOf(Color(0xFF744BD7), Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFFFFEB3B), Color(0xFF2196F3), Color(0xFF9C27B0))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            if (total == 0.0) Text("No Data", fontSize = 12.sp, color = Color.Gray)
            else {
                Canvas(modifier = Modifier.size(130.dp)) {
                    var startAngle = -90f
                    val gap = if (data.size > 1) 3f else 0f
                    data.values.forEachIndexed { index, value ->
                        val sweep = (value.toFloat() / total.toFloat()) * 360f
                        drawArc(colors[index % colors.size], startAngle + (gap/2), sweep - gap, true)
                        startAngle += sweep
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.entries.forEachIndexed { index, entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors[index % colors.size]))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(entry.key, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.1f", (entry.value/total)*100)}% • $${entry.value.toInt()}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun LineGraphWithAxes(points: List<Double>) {
    val maxVal = (points.maxOrNull() ?: 1.0).toFloat()
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(start = 45.dp, bottom = 30.dp, end = 10.dp)) {
            val w = size.width
            val h = size.height
            drawLine(Color.LightGray, Offset(0f, 0f), Offset(0f, h), 2f)
            drawLine(Color.LightGray, Offset(0f, h), Offset(w, h), 2f)
            val dist = w / (points.size - 1)
            val p = points.mapIndexed { i, v -> Offset(i * dist, h - (v.toFloat() / maxVal) * h) }
            val path = Path().apply {
                moveTo(p.first().x, p.first().y)
                p.forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, Color(0xFF4CAF50), style = Stroke(5f))
            p.forEach { drawCircle(Color(0xFF4CAF50), 6f, it) }
        }
        Column(modifier = Modifier.fillMaxHeight().padding(bottom = 30.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text("$${maxVal.toInt()}", fontSize = 10.sp, color = Color.Gray)
            Text("$0", fontSize = 10.sp, color = Color.Gray)
        }
    }
}
