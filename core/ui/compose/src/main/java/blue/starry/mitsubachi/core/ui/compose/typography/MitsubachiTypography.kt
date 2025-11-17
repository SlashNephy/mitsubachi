package blue.starry.mitsubachi.core.ui.compose.typography

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

object MitsubachiTypography {
  private val baseline = Typography()

  val English by lazy {
    baseline.with(
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
    baseline.with(
      fontFamily = FontFamily(
        // JP → KR の順に試行する。JP フォントには欧文フォントも含まれるので無印フォントを読み込む必要はない
        listOf(MitsubachiFont.IBMPlexSansJP, MitsubachiFont.IBMPlexSansKR).flatten(),
      ),
    )
  }

  val Korean by lazy {
    baseline.with(
      fontFamily = FontFamily(
        // KR → JP の順に試行する。KR フォントには欧文グリフも含まれるので欧文フォントを読み込む必要はない
        listOf(MitsubachiFont.IBMPlexSansKR, MitsubachiFont.IBMPlexSansJP).flatten(),
      ),
    )
  }

  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  private fun Typography.with(fontFamily: FontFamily): Typography {
    return copy(
      displayLarge = displayLarge.copy(fontFamily = fontFamily),
      displayMedium = displayMedium.copy(fontFamily = fontFamily),
      displaySmall = displaySmall.copy(fontFamily = fontFamily),
      headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
      headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
      headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
      titleLarge = titleLarge.copy(fontFamily = fontFamily),
      titleMedium = titleMedium.copy(fontFamily = fontFamily),
      titleSmall = titleSmall.copy(fontFamily = fontFamily),
      bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
      bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
      bodySmall = bodySmall.copy(fontFamily = fontFamily),
      labelLarge = labelLarge.copy(fontFamily = fontFamily),
      labelMedium = labelMedium.copy(fontFamily = fontFamily),
      labelSmall = labelSmall.copy(fontFamily = fontFamily),
      displayLargeEmphasized = displayLargeEmphasized.copy(fontFamily = fontFamily),
      displayMediumEmphasized = displayMediumEmphasized.copy(fontFamily = fontFamily),
      displaySmallEmphasized = displaySmallEmphasized.copy(fontFamily = fontFamily),
      headlineLargeEmphasized = headlineLargeEmphasized.copy(fontFamily = fontFamily),
      headlineMediumEmphasized = headlineMediumEmphasized.copy(fontFamily = fontFamily),
      headlineSmallEmphasized = headlineSmallEmphasized.copy(fontFamily = fontFamily),
      titleLargeEmphasized = titleLargeEmphasized.copy(fontFamily = fontFamily),
      titleMediumEmphasized = titleMediumEmphasized.copy(fontFamily = fontFamily),
      titleSmallEmphasized = titleSmallEmphasized.copy(fontFamily = fontFamily),
      bodyLargeEmphasized = bodyLargeEmphasized.copy(fontFamily = fontFamily),
      bodyMediumEmphasized = bodyMediumEmphasized.copy(fontFamily = fontFamily),
      bodySmallEmphasized = bodySmallEmphasized.copy(fontFamily = fontFamily),
      labelLargeEmphasized = labelLargeEmphasized.copy(fontFamily = fontFamily),
      labelMediumEmphasized = labelMediumEmphasized.copy(fontFamily = fontFamily),
      labelSmallEmphasized = labelSmallEmphasized.copy(fontFamily = fontFamily),
    )
  }
}
