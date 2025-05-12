package com.example.jvent.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ElectricPurple,
    secondary = GrayishBlue,
    tertiary = MintGreen,
    background = Jaguar,
    surface = GrayishBlue,
    onPrimary = OffWhite,
    onSecondary = Gray,
    onTertiary = CharcoalBlack,
    onBackground = OffWhite,
    onSurface = OffWhite,
    onSecondaryContainer = Magenta,
    surfaceContainer = Red,
    tertiaryContainer = Blackone,
    onPrimaryContainer = Blacktwo
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricPurple,  // Tetap sama dengan Light Mode, karena ini adalah warna utama yang ingin di-highlight.
    secondary = GrayishBlue,  // Gunakan warna lebih gelap dari Light Mode.
    tertiary = MintGreen,  // Tertier warna tetap mint, cukup terang untuk kontras.
    background = Black,  // Latar belakang gelap untuk Dark Mode.
    surface = Blackthree,  // Surface yang sedikit lebih terang dari background, tetap gelap.
    onPrimary = OffWhite,  // Teks di atas elemen primer perlu kontras tinggi, hitam cocok di atas ElectricPurple.
    onSecondary = Gray,  // Warna teks untuk elemen sekunder juga hitam.
    onTertiary = Blackone,  // Warna teks untuk elemen tertier tetap hitam.
    onBackground = OffWhite,  // Teks di atas background gelap akan berwarna putih untuk kontras.
    onSurface = OffWhite,  // Teks di atas surface gelap juga putih.
    onSecondaryContainer = ElectricPurple,  // Warna teks pada container sekunder tetap cerah.
    surfaceContainer = Blacktwo,  // Warna container surface lebih gelap agar tidak terlalu kontras.
    tertiaryContainer = Blackone,  // Container untuk warna tertier dengan sedikit warna gelap.
    onPrimaryContainer = OffWhite  // Teks di atas elemen kontainer primer putih untuk kontras.
)

@Composable
fun JventTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() //for changing the top android bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme

        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}