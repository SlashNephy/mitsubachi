package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel

/** 隣接レベル間で最低限確保する輝度差。テストと実装で共有する。 */
const val MINIMUM_ADJACENT_LUMINANCE_DIFFERENCE = 0.02f

// 未踏 (0.0) から居住 (1.0) までの補間比率。等間隔だと低いレベル同士の差が潰れるため序盤を広めに取る
private val LEVEL_FRACTIONS = floatArrayOf(0f, 0.24f, 0.43f, 0.62f, 0.81f, 1f)

/**
 * レベルに対応する塗り色を返す。
 *
 * ライトテーマの primary は暗い色、ダークテーマの primary は明るい色なので、
 * 同じ補間式のままレベルが上がるとライトでは濃く、ダークでは明るくなる。
 * 未踏は surfaceContainerLow そのもので、彩度を持たない。
 */
fun ColorScheme.prefectureLevelColor(level: PrefectureLevel): Color {
  return lerp(surfaceContainerLow, primary, LEVEL_FRACTIONS[level.score])
}
