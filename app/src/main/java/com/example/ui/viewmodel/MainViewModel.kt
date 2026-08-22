package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.models.AttendanceEntity
import com.example.data.models.AttendanceSummary
import com.example.data.models.ManagementStats
import com.example.data.models.StudentEntity
import com.example.data.models.TokenMode
import com.example.data.models.UserEntity
import com.example.data.repository.AttendanceRepository
import com.example.utils.DateUtils
import com.example.utils.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AttendanceRepository(
        userDao = database.userDao(),
        studentDao = database.studentDao(),
        attendanceDao = database.attendanceDao()
    )

    // Current logged in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Loading / Status Message
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    // Token Display State
    private val _tokenMode = MutableStateFlow(TokenMode.DINAMIS)
    val tokenMode: StateFlow<TokenMode> = _tokenMode.asStateFlow()

    private val _currentToken = MutableStateFlow("889922")
    val currentToken: StateFlow<String> = _currentToken.asStateFlow()

    private val _tokenSecondsRemaining = MutableStateFlow(30)
    val tokenSecondsRemaining: StateFlow<Int> = _tokenSecondsRemaining.asStateFlow()

    private var tokenTickerJob: Job? = null

    // Student Form States
    val allStudents = repository.allStudents
    private val _selectedStudentNisn = MutableStateFlow("")
    val selectedStudentNisn: StateFlow<String> = _selectedStudentNisn.asStateFlow()

    private val _selectedAttendanceStatus = MutableStateFlow("Hadir")
    val selectedAttendanceStatus: StateFlow<String> = _selectedAttendanceStatus.asStateFlow()

    private val _studentTokenInput = MutableStateFlow("")
    val studentTokenInput: StateFlow<String> = _studentTokenInput.asStateFlow()

    // GPS Location State for Student
    private val _gpsSimulationMode = MutableStateFlow(false)
    val gpsSimulationMode: StateFlow<Boolean> = _gpsSimulationMode.asStateFlow()

    private val _lastDetectedDistance = MutableStateFlow<Double?>(null)
    val lastDetectedDistance: StateFlow<Double?> = _lastDetectedDistance.asStateFlow()

    // Wali Kelas & Sekretaris State
    private val _waliSelectedDate = MutableStateFlow(DateUtils.getTodayDateString())
    val waliSelectedDate: StateFlow<String> = _waliSelectedDate.asStateFlow()

    private val _waliAttendanceList = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val waliAttendanceList: StateFlow<List<AttendanceEntity>> = _waliAttendanceList.asStateFlow()

    private val _waliStudentsList = MutableStateFlow<List<StudentEntity>>(emptyList())
    val waliStudentsList: StateFlow<List<StudentEntity>> = _waliStudentsList.asStateFlow()

    private val _waliMonthlyReport = MutableStateFlow<List<AttendanceSummary>>(emptyList())
    val waliMonthlyReport: StateFlow<List<AttendanceSummary>> = _waliMonthlyReport.asStateFlow()

    private val _waliReportMonth = MutableStateFlow(DateUtils.getCurrentMonthString())
    val waliReportMonth: StateFlow<String> = _waliReportMonth.asStateFlow()

    private val _waliReportYear = MutableStateFlow(DateUtils.getCurrentYearString())
    val waliReportYear: StateFlow<String> = _waliReportYear.asStateFlow()

    // Kepsek & Wakepsek Management Dashboard States
    private val _kepsekFilterDate = MutableStateFlow("")
    val kepsekFilterDate: StateFlow<String> = _kepsekFilterDate.asStateFlow()

    private val _kepsekFilterMonth = MutableStateFlow("ALL")
    val kepsekFilterMonth: StateFlow<String> = _kepsekFilterMonth.asStateFlow()

    private val _kepsekFilterYear = MutableStateFlow(DateUtils.getCurrentYearString())
    val kepsekFilterYear: StateFlow<String> = _kepsekFilterYear.asStateFlow()

    private val _kepsekFilterGrade = MutableStateFlow("ALL")
    val kepsekFilterGrade: StateFlow<String> = _kepsekFilterGrade.asStateFlow()

    private val _kepsekFilterMajor = MutableStateFlow("ALL")
    val kepsekFilterMajor: StateFlow<String> = _kepsekFilterMajor.asStateFlow()

    private val _managementStats = MutableStateFlow(ManagementStats())
    val managementStats: StateFlow<ManagementStats> = _managementStats.asStateFlow()

    private val _managementRecords = MutableStateFlow<List<AttendanceSummary>>(emptyList())
    val managementRecords: StateFlow<List<AttendanceSummary>> = _managementRecords.asStateFlow()

    private val _availableYears = MutableStateFlow<List<String>>(listOf(DateUtils.getCurrentYearString()))
    val availableYears: StateFlow<List<String>> = _availableYears.asStateFlow()

    // Admin Users State
    val allUsers = repository.allUsers

    init {
        viewModelScope.launch {
            DatabaseInitializer.seedInitialData(database)
            startTokenTicker()
            loadAvailableYears()
        }
    }

    private fun startTokenTicker() {
        tokenTickerJob?.cancel()
        tokenTickerJob = viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val token = repository.generateCurrentToken(_tokenMode.value)
                _currentToken.value = token

                if (_tokenMode.value == TokenMode.DINAMIS) {
                    val currentPeriodSec = ((now / 1000) % 30).toInt()
                    _tokenSecondsRemaining.value = 30 - currentPeriodSec
                } else {
                    _tokenSecondsRemaining.value = 30
                }
                delay(1000)
            }
        }
    }

    fun setTokenMode(mode: TokenMode) {
        _tokenMode.value = mode
        _currentToken.value = repository.generateCurrentToken(mode)
    }

    fun setGpsSimulationMode(enabled: Boolean) {
        _gpsSimulationMode.value = enabled
    }

    private suspend fun loadAvailableYears() {
        val years = repository.getAvailableYears()
        _availableYears.value = if (years.isEmpty()) listOf(DateUtils.getCurrentYearString()) else years
    }

    // AUTHENTICATION
    fun login(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Memeriksa kredensial..."
            val result = repository.login(username, password)
            _isLoading.value = false
            _statusMessage.value = null

            result.onSuccess { user ->
                _currentUser.value = user
                onUserLoggedIn(user)
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message ?: "Login gagal")
            }
        }
    }

    fun quickLoginAs(role: String) {
        viewModelScope.launch {
            val user = when (role) {
                "Admin" -> UserEntity("admin", "admin123", "Admin", "Administrator Sekolah", "-", "-", "Aktif")
                "Kepala Sekolah" -> UserEntity("kepsek", "kepsek123", "Kepala Sekolah", "Drs. H. M. Yusuf, M.Pd", "-", "-", "Aktif")
                "Wakil Kepala Sekolah" -> UserEntity("wakepsek", "wakepsek123", "Wakil Kepala Sekolah", "Drs. Ahmad Dahlan, M.Si", "-", "-", "Aktif")
                "Guru Wali Kelas" -> UserEntity("wali_x_tjkt", "123456", "Guru Wali Kelas", "Nyoto Budi Putra, S.Kom", "X", "TJKT", "Aktif")
                "Sekretaris" -> UserEntity("sek_x_tjkt", "123456", "Sekretaris", "Sekretaris Kelas X-TJKT", "X", "TJKT", "Aktif")
                "Display Barcode", "Barcode" -> UserEntity("displaybarcode", "barcode123", "Barcode", "Layar Display Barcode Sekolah", "-", "-", "Aktif")
                "Siswa" -> UserEntity("1001", "123", "Siswa", "Ahmad Rizky", "X", "TJKT", "Aktif")
                else -> null
            }
            if (user != null) {
                _currentUser.value = user
                onUserLoggedIn(user)
            }
        }
    }

    private fun onUserLoggedIn(user: UserEntity) {
        when (user.role) {
            "Guru Wali Kelas", "Sekretaris" -> {
                loadWaliClassData(user.kelas, user.jurusan)
            }
            "Kepala Sekolah", "Wakil Kepala Sekolah" -> {
                loadManagementData()
            }
            "Siswa" -> {
                _selectedStudentNisn.value = user.username
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _studentTokenInput.value = ""
        _lastDetectedDistance.value = null
    }

    // STUDENT ATTENDANCE SUBMISSION
    fun setStudentNisn(nisn: String) {
        _selectedStudentNisn.value = nisn
    }

    fun setStudentStatus(status: String) {
        _selectedAttendanceStatus.value = status
    }

    fun setStudentTokenInput(token: String) {
        _studentTokenInput.value = token
    }

    fun submitStudentAttendance(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onError("Sesi pengguna tidak valid!")
            return
        }

        val tokenInput = _studentTokenInput.value.trim()
        if (tokenInput.isEmpty()) {
            onError("Silakan masukkan Token Absensi 6-digit yang tertera di layar display sekolah!")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Memverifikasi lokasi GPS perangkat..."

            // GPS Check
            var distance = 0.0
            if (!_gpsSimulationMode.value) {
                val location = LocationHelper.getCurrentLocation(context)
                if (location != null) {
                    distance = LocationHelper.calculateDistanceMeters(
                        location.latitude,
                        location.longitude
                    )
                    _lastDetectedDistance.value = distance
                    if (distance > LocationHelper.MAX_RADIUS_METERS) {
                        _isLoading.value = false
                        _statusMessage.value = null
                        onError("Gagal Absen! Jarak Anda (${distance.toInt()} meter) berada di luar radius ${LocationHelper.MAX_RADIUS_METERS.toInt()} meter dari sekolah.")
                        return@launch
                    }
                } else {
                    // Fallback if GPS not available
                    distance = 25.0
                    _lastDetectedDistance.value = distance
                }
            } else {
                distance = 15.0 // Simulation at school
                _lastDetectedDistance.value = distance
            }

            _statusMessage.value = "Memvalidasi token dan menyimpan data..."
            val result = repository.submitStudentAttendance(
                nisn = if (user.role == "Siswa") user.username else _selectedStudentNisn.value,
                nama = user.nama,
                kelas = user.kelas,
                jurusan = user.jurusan,
                tanggal = DateUtils.getTodayDateString(),
                status = _selectedAttendanceStatus.value,
                scannedToken = tokenInput
            )

            _isLoading.value = false
            _statusMessage.value = null

            result.onSuccess { msg ->
                _studentTokenInput.value = ""
                onSuccess(msg)
                _snackbarEvent.emit(msg)
            }.onFailure { err ->
                onError(err.message ?: "Presensi gagal dikirim")
            }
        }
    }

    // WALI KELAS & SEKRETARIS ACTIONS
    fun setWaliSelectedDate(date: String) {
        _waliSelectedDate.value = date
        val user = _currentUser.value ?: return
        loadAttendanceForDate(user.kelas, user.jurusan, date)
    }

    fun loadWaliClassData(kelas: String, jurusan: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _waliStudentsList.value = repository.getStudents(kelas, jurusan)
            loadAttendanceForDate(kelas, jurusan, _waliSelectedDate.value)
            loadWaliMonthlyReport()
            _isLoading.value = false
        }
    }

    private fun loadAttendanceForDate(kelas: String, jurusan: String, date: String) {
        viewModelScope.launch {
            _waliAttendanceList.value = repository.getAttendanceForDate(kelas, jurusan, date)
        }
    }

    fun saveSingleAttendance(
        student: StudentEntity,
        status: String,
        onDone: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        val date = _waliSelectedDate.value
        val note = if (user.role == "Sekretaris") "Menunggu Verifikasi Wali Kelas" else "Terverifikasi Wali Kelas"

        val entity = AttendanceEntity(
            id = "ATT_${student.nisn}_$date",
            tanggal = date,
            nisn = student.nisn,
            nama = student.nama,
            kelas = user.kelas,
            jurusan = user.jurusan,
            status = status,
            catatan = note,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveAttendanceRecord(entity)
            loadAttendanceForDate(user.kelas, user.jurusan, date)
            _snackbarEvent.emit("Status ${student.nama} disimpan ($status)")
            onDone()
        }
    }

    fun setWaliReportMonth(month: String) {
        _waliReportMonth.value = month
        loadWaliMonthlyReport()
    }

    fun setWaliReportYear(year: String) {
        _waliReportYear.value = year
        loadWaliMonthlyReport()
    }

    fun loadWaliMonthlyReport() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val m = _waliReportMonth.value
            val y = _waliReportYear.value
            val searchMonth = if (m != "ALL" && y != "ALL") "$y-$m" else "ALL"
            _waliMonthlyReport.value = repository.getWaliMonthlyReport(user.kelas, user.jurusan, searchMonth)
        }
    }

    // STUDENT CRUD
    fun saveStudent(student: StudentEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.saveStudent(student)
            val user = _currentUser.value
            if (user != null && (user.role == "Guru Wali Kelas" || user.role == "Sekretaris")) {
                loadWaliClassData(user.kelas, user.jurusan)
            }
            _snackbarEvent.emit("Data siswa ${student.nama} berhasil disimpan")
            onDone()
        }
    }

    fun deleteStudent(student: StudentEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteStudent(student.id, student.nisn)
            val user = _currentUser.value
            if (user != null && (user.role == "Guru Wali Kelas" || user.role == "Sekretaris")) {
                loadWaliClassData(user.kelas, user.jurusan)
            }
            _snackbarEvent.emit("Siswa ${student.nama} berhasil dihapus")
            onDone()
        }
    }

    // MANAGEMENT (KEPSEK & WAKEPSEK)
    fun setKepsekFilterDate(date: String) {
        _kepsekFilterDate.value = date
        loadManagementData()
    }

    fun setKepsekFilterMonth(month: String) {
        _kepsekFilterMonth.value = month
        loadManagementData()
    }

    fun setKepsekFilterYear(year: String) {
        _kepsekFilterYear.value = year
        loadManagementData()
    }

    fun setKepsekFilterGrade(grade: String) {
        _kepsekFilterGrade.value = grade
        loadManagementData()
    }

    fun setKepsekFilterMajor(major: String) {
        _kepsekFilterMajor.value = major
        loadManagementData()
    }

    fun loadManagementData() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getManagementReport(
                filterDate = _kepsekFilterDate.value,
                filterMonth = _kepsekFilterMonth.value,
                filterYear = _kepsekFilterYear.value,
                filterGrade = _kepsekFilterGrade.value,
                filterMajor = _kepsekFilterMajor.value
            )
            _managementStats.value = result.first
            _managementRecords.value = result.second
            _isLoading.value = false
        }
    }

    // ADMIN USER MANAGEMENT
    fun saveUser(user: UserEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.saveUser(user)
            _snackbarEvent.emit("Akun ${user.username} berhasil disimpan")
            onDone()
        }
    }

    fun deleteUser(username: String, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteUser(username)
            result.onSuccess {
                _snackbarEvent.emit("Akun $username berhasil dihapus")
                onDone(true, null)
            }.onFailure { err ->
                onDone(false, err.message)
            }
        }
    }
}
