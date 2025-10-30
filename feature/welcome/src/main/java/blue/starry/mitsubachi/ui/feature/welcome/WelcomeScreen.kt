package blue.starry.mitsubachi.ui.feature.welcome

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun WelcomeScreen(
  onLogin: () -> Unit,
  viewModel: WelcomeScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()

  when (val state = state) {
    is WelcomeScreenViewModel.UiState.Loading -> {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
      ) {
        LoadingIndicator()
      }
    }

    is WelcomeScreenViewModel.UiState.NoAccounts -> {
      OnboardingFlow(
        currentStep = currentStep,
        onNextStep = { viewModel.nextStep() },
        onPreviousStep = { viewModel.previousStep() },
        onSkipToLogin = { viewModel.skipToLogin() },
        onPermissionResult = { granted -> viewModel.onPermissionGranted(granted) },
        onLogin = onLogin,
      )
    }

    is WelcomeScreenViewModel.UiState.HasAccount -> {
      LaunchedEffect(state) {
        onLogin()
      }
    }
  }
}

@Composable
private fun OnboardingFlow(
  currentStep: WelcomeScreenViewModel.OnboardingStep,
  onNextStep: () -> Unit,
  onPreviousStep: () -> Unit,
  onSkipToLogin: () -> Unit,
  onPermissionResult: (Boolean) -> Unit,
  onLogin: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.height(32.dp))

    // Page indicators
    PageIndicators(
      currentStep = currentStep,
      modifier = Modifier.padding(vertical = 16.dp),
    )

    Spacer(modifier = Modifier.weight(1f))

    AnimatedContent(
      targetState = currentStep,
      transitionSpec = {
        val direction = when {
          targetState is WelcomeScreenViewModel.OnboardingStep.Welcome -> -1
          initialState is WelcomeScreenViewModel.OnboardingStep.Login -> -1
          else -> 1
        }
        slideInHorizontally { width -> width * direction } + fadeIn() togetherWith
          slideOutHorizontally { width -> -width * direction } + fadeOut()
      },
      label = "onboarding_step_animation",
    ) { step ->
      when (step) {
        is WelcomeScreenViewModel.OnboardingStep.Welcome -> {
          WelcomeStep(
            onNextStep = onNextStep,
            onSkipToLogin = onSkipToLogin,
          )
        }

        is WelcomeScreenViewModel.OnboardingStep.Permissions -> {
          PermissionsStep(
            onPreviousStep = onPreviousStep,
            onPermissionResult = onPermissionResult,
          )
        }

        is WelcomeScreenViewModel.OnboardingStep.Login -> {
          LoginStep(
            onPreviousStep = onPreviousStep,
            onLogin = onLogin,
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))
  }
}

@Composable
private fun PageIndicators(
  currentStep: WelcomeScreenViewModel.OnboardingStep,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    val steps = listOf(
      WelcomeScreenViewModel.OnboardingStep.Welcome,
      WelcomeScreenViewModel.OnboardingStep.Permissions,
      WelcomeScreenViewModel.OnboardingStep.Login,
    )
    steps.forEach { step ->
      val isActive = step == currentStep
      Surface(
        modifier = Modifier
          .size(width = if (isActive) 24.dp else 8.dp, height = 8.dp)
          .clip(CircleShape),
        color = if (isActive) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.surfaceVariant
        },
      ) {}
    }
  }
}

@Composable
private fun WelcomeStep(
  onNextStep: () -> Unit,
  onSkipToLogin: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Icon(
      imageVector = Icons.Default.WavingHand,
      contentDescription = null,
      modifier = Modifier.size(80.dp),
      tint = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = stringResource(R.string.onboarding_welcome_title),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(R.string.onboarding_welcome_subtitle),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = stringResource(R.string.onboarding_welcome_description),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(48.dp))

    Button(
      onClick = onNextStep,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(stringResource(R.string.onboarding_next))
      Spacer(modifier = Modifier.width(8.dp))
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    TextButton(
      onClick = onSkipToLogin,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(stringResource(R.string.onboarding_skip))
    }
  }
}

@Composable
private fun PermissionsStep(
  onPreviousStep: () -> Unit,
  onPermissionResult: (Boolean) -> Unit,
) {
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = { permissions ->
      val granted = permissions.values.any { it }
      onPermissionResult(granted)
    },
  )

  PermissionsStepContent(
    onRequestPermission = {
      permissionLauncher.launch(
        arrayOf(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ),
      )
    },
    onPreviousStep = onPreviousStep,
    onSkip = { onPermissionResult(false) },
  )
}

@Composable
@Suppress("LongMethod")
private fun PermissionsStepContent(
  onRequestPermission: () -> Unit,
  onPreviousStep: () -> Unit,
  onSkip: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Icon(
      imageVector = Icons.Default.LocationOn,
      contentDescription = null,
      modifier = Modifier.size(80.dp),
      tint = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = stringResource(R.string.onboarding_permissions_title),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(R.string.onboarding_permissions_subtitle),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = stringResource(R.string.onboarding_permissions_description),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(48.dp))

    Button(
      onClick = onRequestPermission,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(stringResource(R.string.onboarding_permissions_grant))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      OutlinedButton(
        onClick = onPreviousStep,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.onboarding_back))
      }

      TextButton(
        onClick = onSkip,
      ) {
        Text(stringResource(R.string.onboarding_skip))
      }
    }
  }
}

@Composable
private fun LoginStep(
  onPreviousStep: () -> Unit,
  onLogin: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Filled.Login,
      contentDescription = null,
      modifier = Modifier.size(80.dp),
      tint = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = stringResource(R.string.onboarding_login_title),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(R.string.onboarding_login_subtitle),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = stringResource(R.string.onboarding_login_description),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(48.dp))

    SignInWithFoursquareButton(onSuccess = onLogin)

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedButton(
      onClick = onPreviousStep,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(stringResource(R.string.onboarding_back))
    }
  }
}
