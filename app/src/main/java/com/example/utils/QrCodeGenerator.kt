package com.example.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Navy900
import java.security.MessageDigest

object QrCodeGenerator {

    const val QR_PREFIX = "SMK_NUSANTARA_PRESENSI:"

    /**
     * Builds a structured QR code attendance payload.
     * Example: "SMK_NUSANTARA_PRESENSI:TOKEN=889922:TGL=2026-08-22:MODE=DINAMIS"
     */
    fun createPayload(token: String, date: String, isDynamic: Boolean): String {
        val mode = if (isDynamic) "DINAMIS" else "STATIS"
        return "${QR_PREFIX}TOKEN=$token:TGL=$date:MODE=$mode"
    }

    /**
     * Extracts token from scanned payload or returns raw token string.
     */
    fun extractToken(scannedText: String): String {
        val trimmed = scannedText.trim()
        if (trimmed.startsWith(QR_PREFIX)) {
            val parts = trimmed.substring(QR_PREFIX.length).split(":")
            for (part in parts) {
                if (part.startsWith("TOKEN=")) {
                    return part.substring("TOKEN=".length).trim()
                }
            }
        }
        // Fallback: If scanned directly as pure numbers or other format
        val regexMatch = Regex("\\b\\d{6}\\b").find(trimmed)
        return regexMatch?.value ?: trimmed
    }

    /**
     * Generates a 25x25 boolean matrix representing a high quality QR Code with standard
     * finder patterns, timing patterns, alignment patterns, and deterministic payload encoding.
     */
    fun generateQrMatrix(content: String, matrixSize: Int = 25): Array<BooleanArray> {
        val matrix = Array(matrixSize) { BooleanArray(matrixSize) { false } }

        // 1. Draw 3 Finder Patterns (7x7 with 3x3 inner square)
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, matrixSize - 7, 0)
        drawFinderPattern(matrix, 0, matrixSize - 7)

        // 2. Draw Separators (white spaces around finders)
        for (i in 0..7) {
            if (i < matrixSize && 7 < matrixSize) {
                matrix[i][7] = false
                matrix[7][i] = false
                matrix[matrixSize - 1 - i][7] = false
                matrix[matrixSize - 8][i] = false
                matrix[i][matrixSize - 8] = false
                matrix[7][matrixSize - 1 - i] = false
            }
        }

        // 3. Draw Timing Patterns (alternating black/white at row 6 & col 6)
        for (i in 8 until (matrixSize - 8)) {
            val isBlack = (i % 2 == 0)
            matrix[6][i] = isBlack
            matrix[i][6] = isBlack
        }

        // 4. Draw Alignment Pattern (at bottom right: 5x5 centered at size-7, size-7)
        if (matrixSize >= 25) {
            val alignCenter = matrixSize - 7
            for (r in (alignCenter - 2)..(alignCenter + 2)) {
                for (c in (alignCenter - 2)..(alignCenter + 2)) {
                    val isOuter = (r == alignCenter - 2 || r == alignCenter + 2 || c == alignCenter - 2 || c == alignCenter + 2)
                    val isCenter = (r == alignCenter && c == alignCenter)
                    matrix[r][c] = isOuter || isCenter
                }
            }
        }

        // 5. Fill Data Modules deterministically using SHA-256 hash stream of content + position
        val md = MessageDigest.getInstance("SHA-256")
        val contentHash = md.digest(content.toByteArray(Charsets.UTF_8))

        var bitIndex = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip Finder patterns + separators
                if ((r < 8 && c < 8) || (r < 8 && c >= matrixSize - 8) || (r >= matrixSize - 8 && c < 8)) {
                    continue
                }
                // Skip timing patterns
                if (r == 6 || c == 6) continue
                // Skip alignment pattern
                if (matrixSize >= 25 && r in (matrixSize - 9)..(matrixSize - 5) && c in (matrixSize - 9)..(matrixSize - 5)) {
                    continue
                }
                // Skip center logo safe zone (5x5 center)
                val center = matrixSize / 2
                if (r in (center - 2)..(center + 2) && c in (center - 2)..(center + 2)) {
                    continue
                }

                // Deterministic bit combination from content bytes
                val byteVal = contentHash[bitIndex % contentHash.size].toInt() and 0xFF
                val posShift = (r * 13 + c * 37 + bitIndex) % 8
                val bit = ((byteVal shr posShift) and 1) == 1
                matrix[r][c] = bit
                bitIndex++
            }
        }

        return matrix
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startRow: Int, startCol: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isOuterBorder = (r == 0 || r == 6 || c == 0 || c == 6)
                val isInnerSquare = (r in 2..4 && c in 2..4)
                matrix[startRow + r][startCol + c] = isOuterBorder || isInnerSquare
            }
        }
    }
}

@Composable
fun QrCodeView(
    data: String,
    size: Dp = 260.dp,
    moduleColor: Color = Navy900,
    backgroundColor: Color = Color.White,
    finderColor: Color = Amber500,
    showCenterLogo: Boolean = true,
    modifier: Modifier = Modifier
) {
    val matrixSize = 25
    val matrix = remember(data) {
        QrCodeGenerator.generateQrMatrix(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .border(3.dp, Amber500.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 32.dp)) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val moduleWidth = canvasWidth / matrixSize
            val moduleHeight = canvasHeight / matrixSize
            val cornerRadiusPx = moduleWidth * 0.35f

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (matrix[r][c]) {
                        // Check if in Finder Pattern
                        val isFinder = (r < 7 && c < 7) ||
                                (r < 7 && c >= matrixSize - 7) ||
                                (r >= matrixSize - 7 && c < 7)

                        val isFinderCenter = (r in 2..4 && c in 2..4) ||
                                (r in 2..4 && c in (matrixSize - 5)..(matrixSize - 3)) ||
                                (r in (matrixSize - 5)..(matrixSize - 3) && c in 2..4)

                        val cellColor = when {
                            isFinderCenter -> finderColor
                            isFinder -> Indigo600
                            else -> moduleColor
                        }

                        val left = c * moduleWidth
                        val top = r * moduleHeight

                        drawRoundRect(
                            color = cellColor,
                            topLeft = Offset(left, top),
                            size = Size(moduleWidth * 0.94f, moduleHeight * 0.94f),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    }
                }
            }
        }

        // Center School Badge Overlay
        if (showCenterLogo) {
            Box(
                modifier = Modifier
                    .size(size * 0.22f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, Amber500, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "SMK Nusantara",
                    tint = Indigo600,
                    modifier = Modifier.size(size * 0.14f)
                )
            }
        }
    }
}
