package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ManagementStats
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Purple600
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import kotlin.math.max

@Composable
fun AttendanceBarChartCard(
    stats: ManagementStats,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("Hadir", stats.hadir, Emerald500),
        Triple("PKL", stats.pkl, Purple600),
        Triple("Izin", stats.izin, Blue600),
        Triple("Sakit", stats.sakit, Amber500),
        Triple("Alpa", stats.alpa, Rose500)
    )

    val maxValue = max(1, items.maxOfOrNull { it.second } ?: 1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Statistik Distribusi Kehadiran",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                items.forEach { (label, count, color) ->
                    val ratio = count.toFloat() / maxValue.toFloat()
                    val animatedHeight by animateFloatAsState(
                        targetValue = ratio,
                        animationSpec = tween(durationMillis = 600),
                        label = label
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(fraction = max(0.05f, animatedHeight))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(color)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate600
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceDoughnutCard(
    stats: ManagementStats,
    modifier: Modifier = Modifier
) {
    val total = stats.totalRecords
    val hadirCount = stats.hadir + stats.pkl
    val absenCount = stats.izin + stats.sakit + stats.alpa

    val hadirRatio = if (total > 0) hadirCount.toFloat() / total.toFloat() else 0f
    val izinRatio = if (total > 0) stats.izin.toFloat() / total.toFloat() else 0f
    val sakitRatio = if (total > 0) stats.sakit.toFloat() / total.toFloat() else 0f
    val alpaRatio = if (total > 0) stats.alpa.toFloat() / total.toFloat() else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rasio Presensi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(130.dp)
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 24.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(diameter, diameter)

                    if (total == 0) {
                        drawArc(
                            color = Color(0xFFE2E8F0),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                    } else {
                        var startAngle = -90f

                        // Hadir + PKL
                        val sweepHadir = hadirRatio * 360f
                        drawArc(
                            color = Emerald500,
                            startAngle = startAngle,
                            sweepAngle = sweepHadir,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepHadir

                        // Izin
                        val sweepIzin = izinRatio * 360f
                        drawArc(
                            color = Blue600,
                            startAngle = startAngle,
                            sweepAngle = sweepIzin,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepIzin

                        // Sakit
                        val sweepSakit = sakitRatio * 360f
                        drawArc(
                            color = Amber500,
                            startAngle = startAngle,
                            sweepAngle = sweepSakit,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepSakit

                        // Alpa
                        val sweepAlpa = alpaRatio * 360f
                        drawArc(
                            color = Rose500,
                            startAngle = startAngle,
                            sweepAngle = sweepAlpa,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${String.format("%.1f", stats.persentaseHadir)}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate800
                    )
                    Text(
                        text = "Kehadiran",
                        fontSize = 9.sp,
                        color = Slate400,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Emerald500, label = "Hadir", count = hadirCount)
                LegendItem(color = Blue600, label = "Izin", count = stats.izin)
                LegendItem(color = Amber500, label = "Sakit", count = stats.sakit)
                LegendItem(color = Rose500, label = "Alpa", count = stats.alpa)
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label ($count)",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate700
        )
    }
}
