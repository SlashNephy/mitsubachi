package blue.starry.mitsubachi.core.ui.compose.typography

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
}
