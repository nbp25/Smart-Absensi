package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.models.AttendanceEntity
import com.example.data.models.AttendanceSummary
import com.example.data.models.StudentEntity
import com.example.data.models.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue600
import com.example.ui.theme.Emerald100
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

enum class WaliTab {
    INPUT,
    REKAP,
    SISWA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaliKelasScreen(
    user: UserEntity,
    students: List<StudentEntity>,
    attendanceRecords: List<AttendanceEntity>,
    monthlyReport: List<AttendanceSummary>,
    selectedDate: String,
    onDateChange: (String) -> Unit,
    reportMonth: String,
    onReportMonthChange: (String) -> Unit,
    reportYear: String,
    onReportYearChange: (String) -> Unit,
    availableYears: List<String>,
    onSaveSingleAttendance: (StudentEntity, String, () -> Unit) -> Unit,
    onSaveStudent: (StudentEntity, () -> Unit) -> Unit,
    onDeleteStudent: (StudentEntity, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(WaliTab.INPUT) }
    val isSekretaris = user.role == "Sekretaris"

    var showStudentDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var studentToDelete by remember { mutableStateOf<StudentEntity?>(null) }

    val attendanceMap = remember(attendanceRecords) {
        attendanceRecords.associateBy { it.nisn }
    }

    // Local selected statuses before saving
    val localStatuses = remember { mutableStateMapOf<String, String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
    ) {
        // Banner info
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
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
                            text = if (isSekretaris) "Panel Sekretaris Kelas" else "Panel Guru Wali Kelas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate800
                        )
                        Text(
                            text = "Kelas ${user.kelas} - Jurusan ${user.jurusan} (${user.nama})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600
                        )
                    }

                    Surface(
                        color = if (isSekretaris) Purple100 else Blue100,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${students.size} Siswa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSekretaris) Purple600 else Blue600,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton(
                        text = "Input Absensi",
                        icon = Icons.Default.CalendarToday,
                        isSelected = currentTab == WaliTab.INPUT,
                        onClick = { currentTab = WaliTab.INPUT },
                        modifier = Modifier.weight(1f)
                    )

                    if (!isSekretaris) {
                        TabButton(
                            text = "Rekap Bulanan",
                            icon = Icons.Default.Summarize,
                            isSelected = currentTab == WaliTab.REKAP,
                            onClick = { currentTab = WaliTab.REKAP },
                            modifier = Modifier.weight(1f)
                        )

                        TabButton(
                            text = "Kelola Siswa",
                            icon = Icons.Default.People,
                            isSelected = currentTab == WaliTab.SISWA,
                            onClick = { currentTab = WaliTab.SISWA },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Tab Content
        when (currentTab) {
            WaliTab.INPUT -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Date header filter
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Tanggal Presensi:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate600
                                    )
                                    Text(
                                        text = "${DateUtils.formatIndonesianDate(selectedDate)} ($selectedDate)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Indigo600
                                    )
                                    if (isSekretaris) {
                                        Text(
                                            text = "Terkunci khusus tanggal hari ini",
                                            fontSize = 10.sp,
                                            color = Slate400
                                        )
                                    }
                                }

                                if (!isSekretaris) {
                                    Surface(
                                        color = Indigo50,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.clickable {
                                            // Toggle between today and yesterday for quick demo
                                            val today = DateUtils.getTodayDateString()
                                            onDateChange(if (selectedDate == today) "2026-08-21" else today)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = Indigo600,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Ubah Tanggal",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Indigo600
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Student items
                    itemsIndexed(students) { index, student ->
                        val record = attendanceMap[student.nisn]
                        val currentStatus = localStatuses[student.nisn] ?: (record?.status ?: "Hadir")
                        val isPending = record?.catatan == "Menunggu Verifikasi Wali Kelas"
                        val isRecorded = record != null && !isPending

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
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
                                                text = student.nama,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate800
                                            )
                                            Text(
                                                text = "NISN: ${student.nisn}",
                                                fontSize = 10.sp,
                                                color = Slate500
                                            )
                                        }
                                    }

                                    // Badge
                                    if (isPending) {
                                        Surface(
                                            color = Amber100,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Menunggu Verifikasi",
                                                color = Amber600,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    } else if (isRecorded) {
                                        Surface(
                                            color = Emerald100,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Tercatat (${record.status})",
                                                color = Emerald600,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = Slate100,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Belum Mengisi",
                                                color = Slate500,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Status chips row
                                val statusList = listOf("Hadir", "PKL", "Izin", "Sakit", "Alpa")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    statusList.forEach { st ->
                                        val isChosen = currentStatus.equals(st, ignoreCase = true)
                                        val chipBg = when {
                                            isChosen && st == "Hadir" -> Emerald600
                                            isChosen && st == "PKL" -> Purple600
                                            isChosen && st == "Izin" -> Blue600
                                            isChosen && st == "Sakit" -> Amber600
                                            isChosen && st == "Alpa" -> Rose600
                                            else -> Slate100
                                        }
                                        val textCol = if (isChosen) Color.White else Slate700

                                        Surface(
                                            color = chipBg,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    localStatuses[student.nisn] = st
                                                }
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = st,
                                                fontSize = 10.sp,
                                                fontWeight = if (isChosen) FontWeight.Black else FontWeight.Bold,
                                                color = textCol,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Action button
                                Button(
                                    onClick = {
                                        onSaveSingleAttendance(student, currentStatus) {}
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPending) Amber600 else Indigo600
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .testTag("save_student_status_${student.nisn}")
                                ) {
                                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPending) "Verifikasi & Simpan ($currentStatus)" else "Simpan Status ($currentStatus)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            WaliTab.REKAP -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Filters
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "Filter Rekapitulasi Presensi",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Month Selector
                                    var monthExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = monthExpanded,
                                        onExpandedChange = { monthExpanded = !monthExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = DateUtils.monthsList.find { it.first == reportMonth }?.second ?: "Semua Bulan",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Bulan", fontSize = 10.sp) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Indigo600,
                                                unfocusedBorderColor = Slate300
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = monthExpanded,
                                            onDismissRequest = { monthExpanded = false }
                                        ) {
                                            DateUtils.monthsList.forEach { (code, name) ->
                                                DropdownMenuItem(
                                                    text = { Text(name, fontSize = 12.sp) },
                                                    onClick = {
                                                        onReportMonthChange(code)
                                                        monthExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Year Selector
                                    var yearExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = yearExpanded,
                                        onExpandedChange = { yearExpanded = !yearExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = reportYear,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Tahun", fontSize = 10.sp) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Indigo600,
                                                unfocusedBorderColor = Slate300
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = yearExpanded,
                                            onDismissRequest = { yearExpanded = false }
                                        ) {
                                            availableYears.forEach { yr ->
                                                DropdownMenuItem(
                                                    text = { Text(yr, fontSize = 12.sp) },
                                                    onClick = {
                                                        onReportYearChange(yr)
                                                        yearExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Rekap List
                    itemsIndexed(monthlyReport) { index, item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                                        Text(
                                            text = "${index + 1}. ",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate400
                                        )
                                        Column {
                                            Text(
                                                text = item.nama,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Slate800
                                            )
                                            Text(
                                                text = "NISN: ${item.nisn}",
                                                fontSize = 10.sp,
                                                color = Slate500
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Indigo50,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${String.format("%.1f", item.persentase)}%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Indigo600,
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
                                    CountStatItem(label = "Hadir", count = item.hadir, color = Emerald600)
                                    CountStatItem(label = "PKL", count = item.pkl, color = Purple600)
                                    CountStatItem(label = "Izin", count = item.izin, color = Blue600)
                                    CountStatItem(label = "Sakit", count = item.sakit, color = Amber600)
                                    CountStatItem(label = "Alpa", count = item.alpa, color = Rose600)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            WaliTab.SISWA -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Siswa Kelas ${user.kelas}-${user.jurusan}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )

                            Button(
                                onClick = {
                                    editingStudent = null
                                    showStudentDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("add_student_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Siswa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    itemsIndexed(students) { index, student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
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
                                            text = student.nama,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "NISN: ${student.nisn} | Akun: ${student.username.ifBlank { student.nisn }} / ${student.password}",
                                            fontSize = 10.sp,
                                            color = Slate500
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            editingStudent = student
                                            showStudentDialog = true
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Indigo600, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { studentToDelete = student }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = Rose600, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    // Add / Edit Student Dialog
    if (showStudentDialog) {
        var nisnVal by remember { mutableStateOf(editingStudent?.nisn ?: "") }
        var namaVal by remember { mutableStateOf(editingStudent?.nama ?: "") }
        var userVal by remember { mutableStateOf(editingStudent?.username ?: "") }
        var passVal by remember { mutableStateOf(editingStudent?.password ?: "123") }

        AlertDialog(
            onDismissRequest = { showStudentDialog = false },
            title = {
                Text(
                    text = if (editingStudent == null) "Tambah Siswa Baru" else "Edit Siswa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nisnVal,
                        onValueChange = { nisnVal = it },
                        label = { Text("NISN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = namaVal,
                        onValueChange = { namaVal = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = userVal,
                        onValueChange = { userVal = it },
                        label = { Text("Username Login (Opsional)") },
                        placeholder = { Text("Kosongkan = otomatis NISN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passVal,
                        onValueChange = { passVal = it },
                        label = { Text("Password Login") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nisnVal.isNotBlank() && namaVal.isNotBlank()) {
                            val st = StudentEntity(
                                id = editingStudent?.id ?: "STD_${System.currentTimeMillis()}",
                                nisn = nisnVal.trim(),
                                nama = namaVal.trim(),
                                kelas = user.kelas,
                                jurusan = user.jurusan,
                                username = userVal.trim().ifBlank { nisnVal.trim() },
                                password = passVal.trim().ifBlank { "123" }
                            )
                            onSaveStudent(st) {
                                showStudentDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStudentDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete confirmation
    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus siswa ${studentToDelete?.nama} (${studentToDelete?.nisn})?") },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDelete?.let { st ->
                            onDeleteStudent(st) {
                                studentToDelete = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) Indigo600 else Slate100,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Slate600,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Slate700
            )
        }
    }
}

@Composable
private fun CountStatItem(
    label: String,
    count: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 14.sp,
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
