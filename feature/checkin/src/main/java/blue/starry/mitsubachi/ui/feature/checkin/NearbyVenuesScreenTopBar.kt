package blue.starry.mitsubachi.ui.feature.checkin

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NearbyVenuesScreenTopBar(
  onBack: () -> Unit,
  viewModel: NearbyVenuesScreenTopBarViewModel = hiltViewModel(),
) {
  rememberCoroutineScope()
  val searchBarState = rememberSearchBarState()

  AppBarWithSearch(
    state = searchBarState,
    inputField = {
      val textFieldState = rememberTextFieldState()

      // SearchBarDefaults.InputField が onChange を提供していないため、LaunchedEffect で監視する
      LaunchedEffect(textFieldState.text) {
        // onChange では debounce を入れる
        viewModel.onSearchQueryChangedDebounced(textFieldState.text.toString())
      }

      SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = {
          viewModel.onSearchQueryChanged(textFieldState.text.toString())
        },
        placeholder = {
          Text("スポットを検索する")
        },
      )
    },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Default.ArrowBack,
          contentDescription = stringResource(blue.starry.mitsubachi.ui.R.string.back_button),
        )
      }
    },
  )
}
