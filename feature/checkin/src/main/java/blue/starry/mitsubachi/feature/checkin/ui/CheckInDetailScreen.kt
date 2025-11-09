package blue.starry.mitsubachi.feature.checkin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.domain.model.CheckIn

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun CheckInDetailScreen(checkIn: CheckIn) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
  ) {
    Text(
      text = checkIn.venue.name,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(8.dp))
    checkIn.user?.also {
      Text(
        text = "by ${it.displayName}",
        modifier = Modifier.fillMaxWidth(),
      )
    }
    checkIn.message?.also { message ->
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = message,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}
