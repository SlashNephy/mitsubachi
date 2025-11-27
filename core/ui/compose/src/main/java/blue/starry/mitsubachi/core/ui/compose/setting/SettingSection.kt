package blue.starry.mitsubachi.core.ui.compose.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.typography.OverrideTextStyle
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols

@Composable
fun SettingSection(
  modifier: Modifier = Modifier,
  title: String? = null,
  content: SettingSectionScope.() -> Unit,
) {
  val scope = remember(content) { SettingSectionScopeImpl().apply(content) }
  var isExpanded by remember { mutableStateOf(true) }

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    title?.also {
      SettingSectionTitle(
        text = title,
        isExpanded = isExpanded,
        onClick = {
          isExpanded = !isExpanded
        },
      )
    }

    AnimatedVisibility(visible = isExpanded) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .clip(ShapeDefaults.Large),
      ) {
        items(scope.items.size) { index ->
          scope.items[index].Composable()

          if (index != scope.items.lastIndex) {
            HorizontalDivider(
              modifier = Modifier
                .fillMaxWidth(),
              thickness = 2.dp,
              color = MaterialTheme.colorScheme.surface,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingSectionTitle(text: String, isExpanded: Boolean, onClick: () -> Unit) {
  val iconAngle by animateFloatAsState(
    targetValue = if (isExpanded) {
      0f
    } else {
      180f
    },
  )

  Row(
    modifier = Modifier
      .padding(start = 8.dp)
      .fillMaxWidth()
      .clickable(onClick = onClick),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Icon(
      painter = painterResource(MaterialSymbols.expand_less),
      contentDescription = if (isExpanded) {
        stringResource(R.string.collapse_button)
      } else {
        stringResource(R.string.expand_button)
      },
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier
        .scale(3 / 4f)
        .rotate(iconAngle),
    )
  }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun SettingItem.Composable() {
  ListItem(
    headlineContent = {
      OverrideTextStyle(
        style = { MaterialTheme.typography.titleSmallEmphasized },
        content = headline,
      )
    },
    modifier = modifier,
    overlineContent = overline?.let {
      @Composable {
        OverrideTextStyle(
          style = { MaterialTheme.typography.bodySmall },
          content = it,
        )
      }
    },
    supportingContent = supporting?.let {
      @Composable {
        OverrideTextStyle(
          style = { MaterialTheme.typography.labelSmall },
        ) {
          Box(modifier = Modifier.padding(top = 4.dp)) {
            it()
          }
        }
      }
    },
    leadingContent = ::LeadingContent,
    trailingContent = trailing,
    tonalElevation = 4.dp,
  )
}

@Composable
private fun SettingItem.LeadingContent() {
  when (leadingIcon) {
    is SettingItem.LeadingIcon.Flat -> {
      Box(
        modifier = Modifier
          .size(32.dp),
      ) {
        Icon(
          painter = painterResource(leadingIcon.id),
          contentDescription = null,
          modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize(3 / 4f),
        )
      }
    }

    is SettingItem.LeadingIcon.Round -> {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.background),
      ) {
        Icon(
          painter = painterResource(leadingIcon.id),
          contentDescription = null,
          modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize(2 / 3f),
        )
      }
    }

    is SettingItem.LeadingIcon.Blank -> {
      Spacer(modifier = Modifier.width(32.dp))
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun SettingSectionPreview() {
  SettingSection(
    title = "Title",
    modifier = Modifier.padding(16.dp),
  ) {
    item(
      headline = {
        Text("Headline")
      },
      overline = {
        Text("Overline")
      },
      leadingIcon = SettingItem.LeadingIcon.Flat(MaterialSymbols.home_rounded),
      trailing = {
        Switch(checked = false, onCheckedChange = {})
      },
    )

    item(
      headline = {
        Text("Headline 2")
      },
      supporting = {
        Text("Supporting")
      },
      leadingIcon = SettingItem.LeadingIcon.Round(MaterialSymbols.location_on_rounded),
    )

    item(
      headline = {
        Text("Headline 3")
      },
      supporting = {
        Text("Supporting")
      },
    )
  }
}
