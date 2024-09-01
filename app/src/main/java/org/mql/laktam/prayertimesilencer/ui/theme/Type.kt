package org.mql.laktam.prayertimesilencer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.mql.laktam.prayertimesilencer.R

// Set of Material typography styles to start with
val airstrip = FontFamily(
    Font(R.font.airstrip),
)

val sparkyStones = FontFamily(
    Font(R.font.sparkystones),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = airstrip,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
displaySmall = TextStyle(// scheduled_silence_times, prayer name, start and stop button
    fontFamily = airstrip,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
),
    titleLarge = TextStyle(// from, to,
        fontFamily = airstrip,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(//for note if service is off
        fontFamily = airstrip,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),


    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)