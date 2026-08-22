package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.data.models.UserEntity
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue600
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Indigo100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Purple100
import com.example.ui.theme.Purple600
import com.example.ui.theme.Rose100
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    users: List<UserEntity>,
    onSaveUser: (UserEntity, () -> Unit) -> Unit,
    onDeleteUser: (String, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUserModal by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Admin Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kelola Akun Pengguna",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate800
                        )
                        Text(
                            text = "Kepsek, Wakepsek, Wali Kelas, Sekretaris, & Display",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }

                    Button(
                        onClick = {
                            editingUser = null
                            showUserModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_user_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Akun", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Users List
        itemsIndexed(users) { index, user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when (user.role) {
                                        "Admin" -> Rose100
                                        "Kepala Sekolah", "Wakil Kepala Sekolah" -> Amber100
                                        "Guru Wali Kelas" -> Blue100
                                        "Sekretaris" -> Purple100
                                        else -> Indigo100
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (user.role) {
                                    "Admin" -> Icons.Default.AdminPanelSettings
                                    "Kepala Sekolah", "Wakil Kepala Sekolah" -> Icons.Default.School
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = when (user.role) {
                                    "Admin" -> Rose600
                                    "Kepala Sekolah", "Wakil Kepala Sekolah" -> Amber600
                                    "Guru Wali Kelas" -> Blue600
                                    "Sekretaris" -> Purple600
                                    else -> Indigo600
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = user.nama,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${user.username}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Indigo600
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${user.role}" + (if (user.kelas != "-") " (${user.kelas}-${user.jurusan})" else ""),
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                editingUser = user
                                showUserModal = true
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Indigo600, modifier = Modifier.size(18.dp))
                        }

                        if (!user.username.equals("admin", ignoreCase = true)) {
                            IconButton(
                                onClick = { userToDelete = user }
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = Rose600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    // Modal Add / Edit User
    if (showUserModal) {
        var usernameVal by remember { mutableStateOf(editingUser?.username ?: "") }
        var passwordVal by remember { mutableStateOf(editingUser?.password ?: "") }
        var namaVal by remember { mutableStateOf(editingUser?.nama ?: "") }
        var roleVal by remember { mutableStateOf(editingUser?.role ?: "Guru Wali Kelas") }
        var kelasVal by remember { mutableStateOf(if (editingUser?.kelas != "-") editingUser?.kelas ?: "X" else "X") }
        var jurusanVal by remember { mutableStateOf(if (editingUser?.jurusan != "-") editingUser?.jurusan ?: "AKL" else "AKL") }

        val rolesList = listOf("Guru Wali Kelas", "Sekretaris", "Kepala Sekolah", "Wakil Kepala Sekolah", "Barcode", "Admin")
        val kelasList = listOf("X", "XI", "XII")
        val jurusanList = listOf("AKL", "MPLB", "TJKT")

        val isClassNeeded = roleVal == "Guru Wali Kelas" || roleVal == "Sekretaris"

        AlertDialog(
            onDismissRequest = { showUserModal = false },
            title = {
                Text(
                    text = if (editingUser == null) "Tambah Akun Pengguna" else "Edit Akun Pengguna",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = usernameVal,
                        onValueChange = { usernameVal = it },
                        label = { Text("Username") },
                        enabled = editingUser == null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = passwordVal,
                        onValueChange = { passwordVal = it },
                        label = { Text("Password") },
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

                    // Role Dropdown
                    var roleExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = !roleExpanded }
                    ) {
                        OutlinedTextField(
                            value = roleVal,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            rolesList.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r) },
                                    onClick = {
                                        roleVal = r
                                        roleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (isClassNeeded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Kelas
                            var kExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = kExpanded,
                                onExpandedChange = { kExpanded = !kExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = kelasVal,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Kelas") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = kExpanded,
                                    onDismissRequest = { kExpanded = false }
                                ) {
                                    kelasList.forEach { k ->
                                        DropdownMenuItem(
                                            text = { Text(k) },
                                            onClick = {
                                                kelasVal = k
                                                kExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Jurusan
                            var jExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = jExpanded,
                                onExpandedChange = { jExpanded = !jExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = jurusanVal,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Jurusan") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = jExpanded,
                                    onDismissRequest = { jExpanded = false }
                                ) {
                                    jurusanList.forEach { j ->
                                        DropdownMenuItem(
                                            text = { Text(j) },
                                            onClick = {
                                                jurusanVal = j
                                                jExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (usernameVal.isNotBlank() && passwordVal.isNotBlank() && namaVal.isNotBlank()) {
                            val user = UserEntity(
                                username = usernameVal.trim(),
                                password = passwordVal.trim(),
                                role = roleVal,
                                nama = namaVal.trim(),
                                kelas = if (isClassNeeded) kelasVal else "-",
                                jurusan = if (isClassNeeded) jurusanVal else "-",
                                statusAkses = "Aktif"
                            )
                            onSaveUser(user) {
                                showUserModal = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUserModal = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete confirmation
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Konfirmasi Hapus Akun", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus akun '${userToDelete?.username}' (${userToDelete?.nama})?") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { u ->
                            onDeleteUser(u.username) { success, msg ->
                                if (success) {
                                    userToDelete = null
                                } else {
                                    actionError = msg
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
