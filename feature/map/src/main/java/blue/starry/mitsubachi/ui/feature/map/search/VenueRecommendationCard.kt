package blue.starry.mitsubachi.ui.feature.map.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.domain.model.VenueRecommendation
import blue.starry.mitsubachi.domain.model.url
import blue.starry.mitsubachi.ui.formatter.LengthUnitFormatter
import blue.starry.mitsubachi.ui.formatter.VenueLocationFormatter
import coil3.compose.AsyncImage

@Composable
@Suppress("LongMethod") // TODO: リファクタリング
fun VenueRecommendationCard(
  recommendation: VenueRecommendation,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column {
      // ベニュー画像と投稿者情報
      recommendation.photo?.also { photo ->
        Box {
          AsyncImage(
            model = photo.url(size = 600),
            contentDescription = recommendation.venue.name,
            modifier = Modifier
              .fillMaxWidth()
              .height(240.dp)
              .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentScale = ContentScale.Crop,
          )

          // 投稿者情報（画像の上に重ねて表示）
          recommendation.tips.firstOrNull()?.userName?.let { userName ->
            Row(
              modifier = Modifier
                .padding(12.dp)
                .background(
                  color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                  shape = RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
              // アバター代わりのアイコン
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                  ),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = userName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
              )
            }
          }
        }
      }

      Column(
        modifier = Modifier.padding(16.dp),
      ) {
        // ベニュー名
        Text(
          text = recommendation.venue.name,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )

        // Tips（最初の1件のみ）
        recommendation.tips.firstOrNull()?.also { tip ->
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = tip.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 距離・住所
        val distance = recommendation.venue.location.distance ?: 0
        Text(
          text = buildString {
            LocaleAware {
              append(LengthUnitFormatter.formatMeters(distance))
            }
            append('・')
            append(VenueLocationFormatter.formatAddress(recommendation.venue.location))
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
