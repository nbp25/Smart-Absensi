package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val role: String, // Admin, Kepala Sekolah, Wakil Kepala Sekolah, Guru Wali Kelas, Sekretaris, Barcode, Siswa
    val nama: String,
    val kelas: String = "-", // X, XI, XII or -
    val jurusan: String = "-", // AKL, MPLB, TJKT or -
    val statusAkses: String = "Aktif"
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val nisn: String,
    val nama: String,
    val kelas: String,
    val jurusan: String,
    val username: String = "",
    val password: String = "123"
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String, // e.g. ATT_1001_2026-08-22
    val tanggal: String, // YYYY-MM-DD
    val nisn: String,
    val nama: String,
    val kelas: String,
    val jurusan: String,
    val status: String, // Hadir, PKL, Izin, Sakit, Alpa
    val catatan: String = "Terverifikasi Wali Kelas",
    val timestamp: Long = System.currentTimeMillis()
)

data class AttendanceSummary(
    val nisn: String,
    val nama: String,
    val kelas: String,
    val jurusan: String,
    var hadir: Int = 0,
    var pkl: Int = 0,
    var izin: Int = 0,
    var sakit: Int = 0,
    var alpa: Int = 0,
    var total: Int = 0,
    val dailyRecords: MutableList<AttendanceRecordItem> = mutableListOf()
) {
    val persentase: Double
        get() = if (total > 0) ((hadir + pkl).toDouble() / total * 100.0) else 0.0
}

data class AttendanceRecordItem(
    val tanggal: String,
    val status: String,
    val catatan: String = ""
)

data class ManagementStats(
    val totalSiswa: Int = 0,
    val hadir: Int = 0,
    val pkl: Int = 0,
    val izin: Int = 0,
    val sakit: Int = 0,
    val alpa: Int = 0,
    val totalRecords: Int = 0,
    val persentaseHadir: Double = 0.0,
    val byMajor: Map<String, MajorStats> = emptyMap()
)

data class MajorStats(
    val hadir: Int = 0,
    val pkl: Int = 0,
    val total: Int = 0
)

enum class TokenMode {
    DINAMIS,
    STATIS
}

enum class AttendanceStatus(val label: String) {
    HADIR("Hadir"),
    PKL("PKL"),
    IZIN("Izin"),
    SAKIT("Sakit"),
    ALPA("Alpa")
}
