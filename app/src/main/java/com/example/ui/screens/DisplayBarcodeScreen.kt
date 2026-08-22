package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.TokenMode
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Indigo100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.utils.DateUtils
import com.example.utils.QrCodeGenerator
import com.example.utils.QrCodeView

@Composable
fun DisplayBarcodeScreen(
    currentToken: String,
    tokenMode: TokenMode,
    secondsRemaining: Int,
    onSetTokenMode: (TokenMode) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val todayDate = remember { DateUtils.getTodayDateString() }
    val qrPayload = remember(currentToken, todayDate, tokenMode) {
        QrCodeGenerator.createPayload(
            token = currentToken,
            date = todayDate,
            isDynamic = (tokenMode == TokenMode.DINAMIS)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Toolbar Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onSetTokenMode(TokenMode.DINAMIS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tokenMode == TokenMode.DINAMIS) Emerald600 else Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mode_dinamis_btn")
                    ) {
                        if (tokenMode == TokenMode.DINAMIS) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Amber500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "Barcode Dinamis (30s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { onSetTokenMode(TokenMode.STATIS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tokenMode == TokenMode.STATIS) Blue600 else Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mode_statis_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Barcode Statis",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("display_logout_btn")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Keluar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // School & Title Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SMK SWASTA NUSANTARA LUBUK PAKAM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (tokenMode == TokenMode.DINAMIS) "DISPLAY BARCODE / QR PRESENSI" else "BARCODE PRESENSI STATIS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Amber500,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Arahkan Scanner Siswa ke QR Code di Bawah Ini",
                    fontSize = 11.sp,
                    color = Slate400
                )

                Spacer(modifier = Modifier.height(14.dp))

                // High-Resolution Scannable QR Code Canvas
                QrCodeView(
                    data = qrPayload,
                    size = 230.dp,
                    moduleColor = Navy950,
                    finderColor = Amber600,
                    showCenterLogo = true,
                    modifier = Modifier.testTag("display_qr_view")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Token Number & Status Indicator Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .border(2.dp, Amber500.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "KODE CADANGAN: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate400,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentToken,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Amber500,
                                letterSpacing = 4.sp
                            )
                        }

                        if (tokenMode == TokenMode.DINAMIS) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { secondsRemaining / 30f },
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Emerald500,
                                trackColor = Color.White.copy(alpha = 0.15f),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Barcode diperbarui otomatis dalam $secondsRemaining detik",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald500
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mode Barcode Statis Tetap (Cadangan Offline)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Blue600
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer instructions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Siswa membuka menu 'Pindai Barcode' di aplikasi untuk absen otomatis.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Indigo100
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lokasi SMK Swasta Nusantara: 3.561349, 98.877914",
                    fontSize = 10.sp,
                    color = Slate500
                )
            }
        }
    }
}
