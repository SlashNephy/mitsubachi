package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

object MitsubachiTypography {
  private val baseline = Typography()

  val English by lazy {
    with(
      baseline,
      fontFamily = FontFamily(
        listOf(
          MitsubachiFont.IBMPlexSans,
          MitsubachiFont.IBMPlexSansJP,
          MitsubachiFont.IBMPlexSansKR,
        ).flatten(),
      ),
    )
  }

  val Japanese by lazy {
    with(
      baseline,
      fontFamily = FontFamily(
        // JP → KR の順に試行する。JP フォントには欧文フォントも含まれるので無印フォントを読み込む必要はない
        listOf(MitsubachiFont.IBMPlexSansJP, MitsubachiFont.IBMPlexSansKR).flatten(),
      ),
    )
  }

  val Korean by lazy {
    with(
      baseline,
      fontFamily = FontFamily(
        // KR → JP の順に試行する。KR フォントには欧文グリフも含まれるので欧文フォントを読み込む必要はない
        listOf(MitsubachiFont.IBMPlexSansKR, MitsubachiFont.IBMPlexSansJP).flatten(),
      ),
    )
  }

  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  internal fun with(typography: Typography, fontFamily: FontFamily): Typography {
    return typography.copy(
      displayLarge = typography.displayLarge.copy(fontFamily = fontFamily),
      displayMedium = typography.displayMedium.copy(fontFamily = fontFamily),
      displaySmall = typography.displaySmall.copy(fontFamily = fontFamily),
      headlineLarge = typography.headlineLarge.copy(fontFamily = fontFamily),
      headlineMedium = typography.headlineMedium.copy(fontFamily = fontFamily),
      headlineSmall = typography.headlineSmall.copy(fontFamily = fontFamily),
      titleLarge = typography.titleLarge.copy(fontFamily = fontFamily),
      titleMedium = typography.titleMedium.copy(fontFamily = fontFamily),
      titleSmall = typography.titleSmall.copy(fontFamily = fontFamily),
      bodyLarge = typography.bodyLarge.copy(fontFamily = fontFamily),
      bodyMedium = typography.bodyMedium.copy(fontFamily = fontFamily),
      bodySmall = typography.bodySmall.copy(fontFamily = fontFamily),
      labelLarge = typography.labelLarge.copy(fontFamily = fontFamily),
      labelMedium = typography.labelMedium.copy(fontFamily = fontFamily),
      labelSmall = typography.labelSmall.copy(fontFamily = fontFamily),
      displayLargeEmphasized = typography.displayLargeEmphasized.copy(fontFamily = fontFamily),
      displayMediumEmphasized = typography.displayMediumEmphasized.copy(fontFamily = fontFamily),
      displaySmallEmphasized = typography.displaySmallEmphasized.copy(fontFamily = fontFamily),
      headlineLargeEmphasized = typography.headlineLargeEmphasized.copy(fontFamily = fontFamily),
      headlineMediumEmphasized = typography.headlineMediumEmphasized.copy(fontFamily = fontFamily),
      headlineSmallEmphasized = typography.headlineSmallEmphasized.copy(fontFamily = fontFamily),
      titleLargeEmphasized = typography.titleLargeEmphasized.copy(fontFamily = fontFamily),
      titleMediumEmphasized = typography.titleMediumEmphasized.copy(fontFamily = fontFamily),
      titleSmallEmphasized = typography.titleSmallEmphasized.copy(fontFamily = fontFamily),
      bodyLargeEmphasized = typography.bodyLargeEmphasized.copy(fontFamily = fontFamily),
      bodyMediumEmphasized = typography.bodyMediumEmphasized.copy(fontFamily = fontFamily),
      bodySmallEmphasized = typography.bodySmallEmphasized.copy(fontFamily = fontFamily),
      labelLargeEmphasized = typography.labelLargeEmphasized.copy(fontFamily = fontFamily),
      labelMediumEmphasized = typography.labelMediumEmphasized.copy(fontFamily = fontFamily),
      labelSmallEmphasized = typography.labelSmallEmphasized.copy(fontFamily = fontFamily),
    )
  }
}
