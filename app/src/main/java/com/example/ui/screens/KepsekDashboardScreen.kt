package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AttendanceSummary
import com.example.data.models.ManagementStats
import com.example.data.models.UserEntity
import com.example.ui.components.AttendanceBarChartCard
import com.example.ui.components.AttendanceDoughnutCard
import com.example.ui.components.KpiCard
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Indigo100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Purple100
import com.example.ui.theme.Purple600
import com.example.ui.theme.Rose100
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekDashboardScreen(
    user: UserEntity,
    stats: ManagementStats,
    records: List<AttendanceSummary>,
    filterDate: String,
    onFilterDateChange: (String) -> Unit,
    filterMonth: String,
    onFilterMonthChange: (String) -> Unit,
    filterYear: String,
    onFilterYearChange: (String) -> Unit,
    filterGrade: String,
    onFilterGradeChange: (String) -> Unit,
    filterMajor: String,
    onFilterMajorChange: (String) -> Unit,
    availableYears: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Management Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Laporan Manajemen Sekolah",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Slate800
                            )
                            Text(
                                text = "${user.role}: ${user.nama}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Indigo600
                            )
                        }

                        Surface(
                            color = Indigo50,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Eksekutif",
                                color = Indigo600,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4 KPI Cards in a 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Total Siswa",
                        value = "${stats.totalSiswa}",
                        icon = Icons.Default.People,
                        accentColor = Indigo600,
                        bgColor = Indigo50,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        title = "% Kehadiran",
                        value = "${String.format("%.1f", stats.persentaseHadir)}%",
                        icon = Icons.Default.Percent,
                        accentColor = Emerald600,
                        bgColor = Emerald50,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Hadir / PKL",
                        value = "${stats.hadir + stats.pkl}",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Blue600,
                        bgColor = Blue50,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        title = "Absen (A/I/S)",
                        value = "${stats.alpa + stats.izin + stats.sakit}",
                        icon = Icons.Default.Warning,
                        accentColor = Rose600,
                        bgColor = Rose50,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Visual Charts
        item {
            AttendanceBarChartCard(stats = stats)
        }

        item {
            AttendanceDoughnutCard(stats = stats)
        }

        // Advanced Filter Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Indigo600,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Filter Presensi Sekolah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                        }

                        if (filterDate.isNotBlank() || filterMonth != "ALL" || filterGrade != "ALL" || filterMajor != "ALL") {
                            TextButton(
                                onClick = {
                                    onFilterDateChange("")
                                    onFilterMonthChange("ALL")
                                    onFilterGradeChange("ALL")
                                    onFilterMajorChange("ALL")
                                }
                            ) {
                                Text("Reset Filter", fontSize = 11.sp, color = Rose600, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date & Month Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Month Dropdown
                        var monthExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = monthExp,
                            onExpandedChange = { monthExp = !monthExp },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = DateUtils.monthsList.find { it.first == filterMonth }?.second ?: "Semua Bulan",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Bulan", fontSize = 10.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = Slate300
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = monthExp,
                                onDismissRequest = { monthExp = false }
                            ) {
                                DateUtils.monthsList.forEach { (c, n) ->
                                    DropdownMenuItem(
                                        text = { Text(n, fontSize = 12.sp) },
                                        onClick = {
                                            onFilterMonthChange(c)
                                            monthExp = false
                                        }
                                    )
                                }
                            }
                        }

                        // Year Dropdown
                        var yearExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = yearExp,
                            onExpandedChange = { yearExp = !yearExp },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = filterYear,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tahun", fontSize = 10.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = Slate300
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = yearExp,
                                onDismissRequest = { yearExp = false }
                            ) {
                                availableYears.forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text(yr, fontSize = 12.sp) },
                                        onClick = {
                                            onFilterYearChange(yr)
                                            yearExp = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grade & Major Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Grade Dropdown
                        val grades = listOf("ALL" to "Semua Kelas", "X" to "Kelas X", "XI" to "Kelas XI", "XII" to "Kelas XII")
                        var gradeExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = gradeExp,
                            onExpandedChange = { gradeExp = !gradeExp },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = grades.find { it.first == filterGrade }?.second ?: "Semua Kelas",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tingkat Kelas", fontSize = 10.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = Slate300
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = gradeExp,
                                onDismissRequest = { gradeExp = false }
                            ) {
                                grades.forEach { (c, n) ->
                                    DropdownMenuItem(
                                        text = { Text(n, fontSize = 12.sp) },
                                        onClick = {
                                            onFilterGradeChange(c)
                                            gradeExp = false
                                        }
                                    )
                                }
                            }
                        }

                        // Major Dropdown
                        val majors = listOf("ALL" to "Semua Jurusan", "AKL" to "AKL", "MPLB" to "MPLB", "TJKT" to "TJKT")
                        var majorExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = majorExp,
                            onExpandedChange = { majorExp = !majorExp },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = majors.find { it.first == filterMajor }?.second ?: "Semua Jurusan",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Jurusan", fontSize = 10.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = majorExp) },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = Slate300
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = majorExp,
                                onDismissRequest = { majorExp = false }
                            ) {
                                majors.forEach { (c, n) ->
                                    DropdownMenuItem(
                                        text = { Text(n, fontSize = 12.sp) },
                                        onClick = {
                                            onFilterMajorChange(c)
                                            majorExp = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Student Rekap Table header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Rekapitulasi Siswa (${records.size} Siswa)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
            }
        }

        // Student items
        itemsIndexed(records) { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Indigo50),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.nama,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Slate800
                                )
                                Text(
                                    text = "Kelas ${item.kelas} - ${item.jurusan} | NISN: ${item.nisn}",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Surface(
                            color = if (item.persentase >= 80.0) Emerald100 else if (item.persentase >= 60.0) Amber100 else Rose100,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", item.persentase)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (item.persentase >= 80.0) Emerald600 else if (item.persentase >= 60.0) Amber600 else Rose600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Slate100)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SmallStatColumn(label = "Hadir", count = item.hadir, color = Emerald600)
                        SmallStatColumn(label = "PKL", count = item.pkl, color = Purple600)
                        SmallStatColumn(label = "Izin", count = item.izin, color = Blue600)
                        SmallStatColumn(label = "Sakit", count = item.sakit, color = Amber600)
                        SmallStatColumn(label = "Alpa", count = item.alpa, color = Rose600)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
private fun SmallStatColumn(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate500
        )
    }
}
