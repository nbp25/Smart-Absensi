package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.LoadingBanner
import com.example.ui.screens.AdminUserManagementScreen
import com.example.ui.screens.DisplayBarcodeScreen
import com.example.ui.screens.KepsekDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.StudentAttendanceScreen
import com.example.ui.screens.WaliKelasScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SmartAbsenApp()
            }
        }
    }
}

@Composable
fun SmartAbsenApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val currentToken by viewModel.currentToken.collectAsStateWithLifecycle()
    val tokenMode by viewModel.tokenMode.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.tokenSecondsRemaining.collectAsStateWithLifecycle()

    val selectedStudentNisn by viewModel.selectedStudentNisn.collectAsStateWithLifecycle()
    val selectedAttendanceStatus by viewModel.selectedAttendanceStatus.collectAsStateWithLifecycle()
    val studentTokenInput by viewModel.studentTokenInput.collectAsStateWithLifecycle()
    val gpsSimulationMode by viewModel.gpsSimulationMode.collectAsStateWithLifecycle()
    val lastDetectedDistance by viewModel.lastDetectedDistance.collectAsStateWithLifecycle()

    val waliSelectedDate by viewModel.waliSelectedDate.collectAsStateWithLifecycle()
    val waliStudentsList by viewModel.waliStudentsList.collectAsStateWithLifecycle()
    val waliAttendanceList by viewModel.waliAttendanceList.collectAsStateWithLifecycle()
    val waliMonthlyReport by viewModel.waliMonthlyReport.collectAsStateWithLifecycle()
    val waliReportMonth by viewModel.waliReportMonth.collectAsStateWithLifecycle()
    val waliReportYear by viewModel.waliReportYear.collectAsStateWithLifecycle()

    val kepsekFilterDate by viewModel.kepsekFilterDate.collectAsStateWithLifecycle()
    val kepsekFilterMonth by viewModel.kepsekFilterMonth.collectAsStateWithLifecycle()
    val kepsekFilterYear by viewModel.kepsekFilterYear.collectAsStateWithLifecycle()
    val kepsekFilterGrade by viewModel.kepsekFilterGrade.collectAsStateWithLifecycle()
    val kepsekFilterMajor by viewModel.kepsekFilterMajor.collectAsStateWithLifecycle()
    val managementStats by viewModel.managementStats.collectAsStateWithLifecycle()
    val managementRecords by viewModel.managementRecords.collectAsStateWithLifecycle()
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()

    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle(initialValue = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (currentUser?.role == "Barcode" || currentUser?.role == "Display Barcode") {
        DisplayBarcodeScreen(
            currentToken = currentToken,
            tokenMode = tokenMode,
            secondsRemaining = secondsRemaining,
            onSetTokenMode = viewModel::setTokenMode,
            onLogout = viewModel::logout,
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Column {
                    AppHeader(
                        currentUser = currentUser,
                        onLogout = viewModel::logout
                    )
                    LoadingBanner(
                        text = statusMessage ?: "Memproses...",
                        visible = isLoading && statusMessage != null
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentUser?.role) {
                    null -> {
                        LoginScreen(
                            isLoading = isLoading,
                            onLoginSubmit = viewModel::login,
                            onQuickLogin = viewModel::quickLoginAs
                        )
                    }

                    "Siswa" -> {
                        StudentAttendanceScreen(
                            user = currentUser!!,
                            selectedStatus = selectedAttendanceStatus,
                            onStatusChange = viewModel::setStudentStatus,
                            tokenInput = studentTokenInput,
                            onTokenInputChange = viewModel::setStudentTokenInput,
                            isSimulationMode = gpsSimulationMode,
                            onSimulationModeChange = viewModel::setGpsSimulationMode,
                            lastDistance = lastDetectedDistance,
                            isLoading = isLoading,
                            onSubmitAttendance = viewModel::submitStudentAttendance
                        )
                    }

                    "Guru Wali Kelas", "Sekretaris" -> {
                        WaliKelasScreen(
                            user = currentUser!!,
                            students = waliStudentsList,
                            attendanceRecords = waliAttendanceList,
                            monthlyReport = waliMonthlyReport,
                            selectedDate = waliSelectedDate,
                            onDateChange = viewModel::setWaliSelectedDate,
                            reportMonth = waliReportMonth,
                            onReportMonthChange = viewModel::setWaliReportMonth,
                            reportYear = waliReportYear,
                            onReportYearChange = viewModel::setWaliReportYear,
                            availableYears = availableYears,
                            onSaveSingleAttendance = { student, status, onDone ->
                                viewModel.saveSingleAttendance(student, status, onDone)
                            },
                            onSaveStudent = viewModel::saveStudent,
                            onDeleteStudent = viewModel::deleteStudent
                        )
                    }

                    "Kepala Sekolah", "Wakil Kepala Sekolah" -> {
                        KepsekDashboardScreen(
                            user = currentUser!!,
                            stats = managementStats,
                            records = managementRecords,
                            filterDate = kepsekFilterDate,
                            onFilterDateChange = viewModel::setKepsekFilterDate,
                            filterMonth = kepsekFilterMonth,
                            onFilterMonthChange = viewModel::setKepsekFilterMonth,
                            filterYear = kepsekFilterYear,
                            onFilterYearChange = viewModel::setKepsekFilterYear,
                            filterGrade = kepsekFilterGrade,
                            onFilterGradeChange = viewModel::setKepsekFilterGrade,
                            filterMajor = kepsekFilterMajor,
                            onFilterMajorChange = viewModel::setKepsekFilterMajor,
                            availableYears = availableYears
                        )
                    }

                    "Admin" -> {
                        AdminUserManagementScreen(
                            users = allUsers,
                            onSaveUser = viewModel::saveUser,
                            onDeleteUser = viewModel::deleteUser
                        )
                    }

                    else -> {
                        LoginScreen(
                            isLoading = isLoading,
                            onLoginSubmit = viewModel::login,
                            onQuickLogin = viewModel::quickLoginAs
                        )
                    }
                }
            }
        }
    }
}
