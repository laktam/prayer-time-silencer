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

val HarmattanFontFamily = FontFamily(
    Font(R.font.harmattan_regular, FontWeight.Normal),
    Font(R.font.harmattan_bold, FontWeight.Bold),
    Font(R.font.harmattan_medium, FontWeight.Medium),
    Font(R.font.harmattan_semibold, FontWeight.SemiBold)
)

val sparkyStones = FontFamily(
    Font(R.font.sparkystones),
)

val Typography = Typography(
    bodyLarge = TextStyle(//start and stop button
        fontFamily = HarmattanFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
displaySmall = TextStyle(// scheduled_silence_times, prayer name,
    fontFamily = HarmattanFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
),
    titleLarge = TextStyle(// from, to,
        fontFamily = HarmattanFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(//for note if service is off
        fontFamily = HarmattanFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
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