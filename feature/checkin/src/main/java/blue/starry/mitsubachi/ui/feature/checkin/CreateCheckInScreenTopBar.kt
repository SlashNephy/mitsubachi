package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.model.primaryCategory
import blue.starry.mitsubachi.ui.formatter.VenueLocationFormatter
import blue.starry.mitsubachi.ui.foundation.VenueCategoryIcon

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun CreateCheckInScreenTopBar(
  venue: Venue,
  onBack: () -> Unit,
) {
  TopAppBar(
    title = {
      val fontSize = 16.sp
      val iconSize = with(LocalDensity.current) {
        fontSize.toDp()
      }

      Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        VenueCategoryIcon(venue.primaryCategory, modifier = Modifier.size(iconSize))
        Text(venue.name, fontSize = fontSize)
      }
    },
    subtitle = {
      Text(VenueLocationFormatter.formatAddress(venue.location))
    },
    modifier = Modifier.clickable(onClick = onBack),
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Default.ArrowBack,
          contentDescription = stringResource(blue.starry.mitsubachi.ui.R.string.back_button),
        )
      }
    },
    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
  )
}
