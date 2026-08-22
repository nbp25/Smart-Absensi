package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.AttendanceEntity
import com.example.data.models.StudentEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY role, nama")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY kelas, jurusan, nama ASC")
    fun getAllStudentsFlow(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY kelas, jurusan, nama ASC")
    suspend fun getAllStudents(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan) ORDER BY nama ASC")
    fun getStudentsByClassFlow(kelas: String, jurusan: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan) ORDER BY nama ASC")
    suspend fun getStudentsByClass(kelas: String, jurusan: String): List<StudentEntity>

    @Query("SELECT * FROM students WHERE nisn = :nisn OR username = :username LIMIT 1")
    suspend fun getStudentByNisnOrUsername(nisn: String, username: String): StudentEntity?

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY tanggal DESC, timestamp DESC")
    fun getAllAttendanceFlow(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY tanggal DESC, timestamp DESC")
    suspend fun getAllAttendance(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE tanggal = :date AND (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan)")
    fun getAttendanceForDateFlow(kelas: String, jurusan: String, date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE tanggal = :date AND (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan)")
    suspend fun getAttendanceForDate(kelas: String, jurusan: String, date: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE nisn = :nisn AND tanggal = :date LIMIT 1")
    suspend fun getAttendanceByStudentAndDate(nisn: String, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE tanggal LIKE :monthPattern AND (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan)")
    suspend fun getAttendanceForMonth(kelas: String, jurusan: String, monthPattern: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE (:kelas = 'ALL' OR kelas = :kelas) AND (:jurusan = 'ALL' OR jurusan = :jurusan)")
    suspend fun getAttendanceForClass(kelas: String, jurusan: String): List<AttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE id = :id")
    suspend fun deleteAttendanceById(id: String)

    @Query("DELETE FROM attendance WHERE nisn = :nisn")
    suspend fun deleteAttendanceByNisn(nisn: String)
}
