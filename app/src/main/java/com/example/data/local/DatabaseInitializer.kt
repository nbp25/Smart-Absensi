package com.example.data.local

import com.example.data.models.AttendanceEntity
import com.example.data.models.StudentEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DatabaseInitializer {

    suspend fun seedInitialData(database: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = database.userDao()
        val studentDao = database.studentDao()
        val attendanceDao = database.attendanceDao()

        if (userDao.getUserCount() == 0) {
            val initialUsers = listOf(
                UserEntity("admin", "admin123", "Admin", "Administrator Sekolah", "-", "-", "Aktif"),
                UserEntity("kepsek", "kepsek123", "Kepala Sekolah", "Drs. H. M. Yusuf, M.Pd", "-", "-", "Aktif"),
                UserEntity("wakepsek", "wakepsek123", "Wakil Kepala Sekolah", "Drs. Ahmad Dahlan, M.Si", "-", "-", "Aktif"),
                UserEntity("wali_x_tjkt", "123456", "Guru Wali Kelas", "Nyoto Budi Putra, S.Kom", "X", "TJKT", "Aktif"),
                UserEntity("wali_x_akl", "123456", "Guru Wali Kelas", "Sri Wahyuni, S.Pd", "X", "AKL", "Aktif"),
                UserEntity("wali_xi_mplb", "123456", "Guru Wali Kelas", "Hendra Siregar, S.Pd", "XI", "MPLB", "Aktif"),
                UserEntity("sek_x_tjkt", "123456", "Sekretaris", "Sekretaris Kelas X-TJKT", "X", "TJKT", "Aktif"),
                UserEntity("displaybarcode", "barcode123", "Barcode", "Layar Display Token Sekolah", "-", "-", "Aktif")
            )
            userDao.insertUsers(initialUsers)
        }

        if (studentDao.getStudentCount() == 0) {
            val initialStudents = listOf(
                StudentEntity("STD1", "1001", "Ahmad Rizky", "X", "TJKT", "ahmad1001", "123"),
                StudentEntity("STD2", "1002", "Budi Santoso", "X", "TJKT", "budi1002", "123"),
                StudentEntity("STD3", "1003", "Cindy Claudia", "X", "TJKT", "cindy1003", "123"),
                StudentEntity("STD4", "1004", "Dinda Maharani", "X", "TJKT", "dinda1004", "123"),
                StudentEntity("STD5", "1005", "Eko Prasetyo", "X", "TJKT", "eko1005", "123"),
                StudentEntity("STD6", "1006", "Fajar Maulana", "X", "AKL", "fajar1006", "123"),
                StudentEntity("STD7", "1007", "Gita Permata", "X", "AKL", "gita1007", "123"),
                StudentEntity("STD8", "1008", "Hadi Wijaya", "X", "AKL", "hadi1008", "123"),
                StudentEntity("STD9", "1009", "Indah Lestari", "XI", "MPLB", "indah1009", "123"),
                StudentEntity("STD10", "1010", "Joko Susilo", "XI", "MPLB", "joko1010", "123"),
                StudentEntity("STD11", "1011", "Kurnia Saputra", "XI", "MPLB", "kurnia1011", "123"),
                StudentEntity("STD12", "1012", "Lestari Anggraini", "XII", "TJKT", "lestari1012", "123"),
                StudentEntity("STD13", "1013", "Muhammad Fadli", "XII", "AKL", "fadli1013", "123"),
                StudentEntity("STD14", "1014", "Nurul Hidayah", "XII", "MPLB", "nurul1014", "123")
            )
            studentDao.insertStudents(initialStudents)

            // Generate some realistic past attendance records for charts and reports
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            val initialAttendance = mutableListOf<AttendanceEntity>()

            // Past 5 days
            for (i in 0..4) {
                cal.time = Date()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                val dateStr = sdf.format(cal.time)

                initialStudents.forEach { student ->
                    val status = when {
                        student.nisn == "1003" && i == 1 -> "Sakit"
                        student.nisn == "1005" && i == 2 -> "Izin"
                        student.nisn == "1008" && i == 3 -> "Alpa"
                        student.kelas == "XII" -> "PKL"
                        else -> "Hadir"
                    }
                    val catatan = if (i == 0 && student.nisn == "1001") "Menunggu Verifikasi Wali Kelas" else "Terverifikasi Wali Kelas"
                    initialAttendance.add(
                        AttendanceEntity(
                            id = "ATT_${student.nisn}_$dateStr",
                            tanggal = dateStr,
                            nisn = student.nisn,
                            nama = student.nama,
                            kelas = student.kelas,
                            jurusan = student.jurusan,
                            status = status,
                            catatan = catatan
                        )
                    )
                }
            }
            attendanceDao.insertAttendanceList(initialAttendance)
        }
    }
}
