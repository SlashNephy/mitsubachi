package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel

// 未踏 (0.0) から居住 (1.0) までの補間比率。
// 等間隔だと低いレベル同士の差が潰れるため序盤を広めに取る。
// ダークテーマでは未踏とレベル 1 の輝度差がもっとも小さくなるので、
// レンダリング比較 (docs/superpowers/assets/prefectures/preview) にもとづき序盤をさらに広げた。
// 上位側の比率は据え置き、レベル 4 とレベル 5 の差はライトテーマでも保つ。
private val LEVEL_FRACTIONS = floatArrayOf(0f, 0.30f, 0.48f, 0.65f, 0.82f, 1f)

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
