package blue.starry.mitsubachi.ui.screen

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.domain.error.AppError
import blue.starry.mitsubachi.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.domain.error.NetworkUnavailableError
import blue.starry.mitsubachi.domain.error.RetryableAppError
import blue.starry.mitsubachi.domain.error.UnauthorizedError
import blue.starry.mitsubachi.ui.R
import io.mockk.mockk
import kotlinx.coroutines.launch

@Composable
fun ErrorScreen(
  exception: Exception,
  onClickRetry: (() -> Unit)? = null,
  viewModel: ErrorScreenViewModel = hiltViewModel(),
) {
  LaunchedEffect(exception) {
    viewModel.report(exception)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(48.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      val (icon, stringResourceId) = rememberResource(exception)

      Icon(icon, contentDescription = null, modifier = Modifier.scale(1.5f))
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        stringResource(stringResourceId),
        textAlign = TextAlign.Center,
        style = TextStyle.Default.copy(lineBreak = LineBreak.Paragraph),
      )

      if (exception.isRetryable() && onClickRetry != null) {
        RetryButton(
          onClick = onClickRetry,
          modifier = Modifier.padding(vertical = 16.dp),
        )
      }
    }
  }
}

@Preview(showSystemUi = true)
@Composable
private fun ErrorScreenPreview() {
  ErrorScreen(
    exception = Exception("Something went wrong"),
    onClickRetry = {},
    viewModel = @Suppress("ViewModelConstructorInComposable") ErrorScreenViewModel(mockk()),
  )
}

private fun Exception.isRetryable(): Boolean {
  return when (this) {
    is AppError -> {
      when (this) {
        is RetryableAppError -> true
        else -> false
      }
    }

    is Exception -> {
      true
    }
  }
}

private data class ErrorResource(val icon: ImageVector, @param:StringRes val stringResourceId: Int)

@Composable
private fun rememberResource(exception: Exception): ErrorResource {
  return when (exception) {
    is AppError -> {
      when (exception) {
        is NetworkTimeoutError -> {
          ErrorResource(Icons.Default.AccessTime, R.string.network_timeout_error)
        }

        is NetworkUnavailableError -> {
          ErrorResource(Icons.Default.NetworkCheck, R.string.network_unavailable_error)
        }

        is UnauthorizedError -> {
          ErrorResource(Icons.Default.Close, R.string.unauthorized_error)
        }
      }
    }

    is Exception -> {
      ErrorResource(Icons.Default.ErrorOutline, R.string.unknown_error)
    }
  }
}

@Preview(showSystemUi = true)
@Composable
private fun NetworkTimeoutErrorScreenPreview() {
  ErrorScreen(
    exception = NetworkTimeoutError(mockk()),
    onClickRetry = {},
    viewModel = @Suppress("ViewModelConstructorInComposable") ErrorScreenViewModel(mockk()),
  )
}

@Preview(showSystemUi = true)
@Composable
private fun NetworkUnavailableErrorScreenPreview() {
  ErrorScreen(
    exception = NetworkUnavailableError(),
    onClickRetry = {},
    viewModel = @Suppress("ViewModelConstructorInComposable") ErrorScreenViewModel(mockk()),
  )
}

@Preview(showSystemUi = true)
@Composable
private fun UnauthorizedErrorScreenPreview() {
  ErrorScreen(
    exception = UnauthorizedError(),
    onClickRetry = {},
    viewModel = @Suppress("ViewModelConstructorInComposable") ErrorScreenViewModel(mockk()),
  )
}

@Composable
private fun RetryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val angle = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()

  Button(
    onClick = {
      scope.launch {
        angle.animateTo(
          targetValue = angle.targetValue + 360f,
          animationSpec = tween(durationMillis = 500),
        )
      }

      onClick()
    },
    modifier = modifier,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        Icons.Default.Refresh,
        contentDescription = null,
        modifier = Modifier.rotate(angle.value),
      )
      Text(stringResource(R.string.retry_button))
    }
  }
}

@Preview
@Composable
private fun RetryButtonPreview() {
  RetryButton(onClick = {})
}
