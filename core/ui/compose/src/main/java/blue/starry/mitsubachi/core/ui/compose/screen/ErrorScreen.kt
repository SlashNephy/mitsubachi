package blue.starry.mitsubachi.core.ui.compose.screen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import blue.starry.mitsubachi.core.domain.error.AppError
import blue.starry.mitsubachi.core.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.core.domain.error.NetworkUnavailableError
import blue.starry.mitsubachi.core.domain.error.RetryableAppError
import blue.starry.mitsubachi.core.domain.error.UnauthorizedError
import blue.starry.mitsubachi.core.ui.compose.R
import blue.starry.mitsubachi.core.ui.compose.error.ErrorFormatterImpl
import blue.starry.mitsubachi.core.ui.compose.error.NoOpErrorReporter
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import kotlinx.coroutines.launch
import kotlinx.io.IOException

@Composable
fun ErrorScreen(
  exception: Exception,
  modifier: Modifier = Modifier,
  onClickRetry: (() -> Unit)? = null,
  viewModel: ErrorScreenViewModel = hiltViewModel(),
) {
  LaunchedEffect(exception) {
    viewModel.report(exception)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(48.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      ErrorIcon(exception, modifier = Modifier.scale(1.5f))

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = remember(exception) { viewModel.format(exception) },
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

@Composable
private fun ErrorIcon(exception: Exception, modifier: Modifier = Modifier) {
  val iconId = when (exception) {
    is AppError -> {
      when (exception) {
        is NetworkTimeoutError -> MaterialSymbols.hourglass_disabled
        is NetworkUnavailableError -> MaterialSymbols.network_check
        is UnauthorizedError -> MaterialSymbols.close
      }
    }

    is Exception -> {
      MaterialSymbols.error_filled
    }
  }

  return Icon(
    painter = painterResource(iconId),
    contentDescription = null,
    modifier = modifier,
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
        painterResource(MaterialSymbols.refresh),
        contentDescription = null,
        modifier = Modifier.rotate(angle.value),
      )
      Text(stringResource(R.string.retry_button))
    }
  }
}

@Composable
private fun rememberPreviewViewModel(): ErrorScreenViewModel {
  val context = LocalContext.current
  return remember {
    ErrorScreenViewModel(
      reporter = NoOpErrorReporter,
      formatter = ErrorFormatterImpl(context),
    )
  }
}

@Preview(showSystemUi = true)
@Composable
private fun GenericErrorScreenPreview() {
  ErrorScreen(
    exception = Exception("Something went wrong"),
    onClickRetry = {},
    viewModel = rememberPreviewViewModel(),
  )
}

@Preview(showSystemUi = true)
@Composable
private fun NetworkTimeoutErrorScreenPreview() {
  ErrorScreen(
    exception = NetworkTimeoutError(IOException("network timeout")),
    onClickRetry = {},
    viewModel = rememberPreviewViewModel(),
  )
}

@Preview(showSystemUi = true)
@Composable
private fun NetworkUnavailableErrorScreenPreview() {
  ErrorScreen(
    exception = NetworkUnavailableError(),
    onClickRetry = {},
    viewModel = rememberPreviewViewModel(),
  )
}

@Preview(showSystemUi = true)
@Composable
private fun UnauthorizedErrorScreenPreview() {
  ErrorScreen(
    exception = UnauthorizedError(),
    onClickRetry = {},
    viewModel = rememberPreviewViewModel(),
  )
}

@Preview
@Composable
private fun RetryButtonPreview() {
  RetryButton(onClick = {})
}
