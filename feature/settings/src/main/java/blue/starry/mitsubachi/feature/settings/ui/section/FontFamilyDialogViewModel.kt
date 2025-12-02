package blue.starry.mitsubachi.feature.settings.ui.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.GoogleWebFont
import blue.starry.mitsubachi.core.domain.usecase.GoogleWebFontClient
import blue.starry.mitsubachi.core.ui.compose.error.onException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FontFamilyDialogViewModel @Inject constructor(
  private val googleWebFontClient: GoogleWebFontClient,
) : ViewModel() {
  companion object {
    val availableCategories = GoogleWebFont.Category.entries.toTypedArray()

    val availableSubsets = arrayOf(
      GoogleWebFont.Subset.Latin,
      GoogleWebFont.Subset.Japanese,
      GoogleWebFont.Subset.Korean,
    )
  }

  sealed interface UiState {
    data object Loading : UiState
    data class Loaded(val fonts: List<GoogleWebFont>) : UiState
    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh() {
    viewModelScope.launch {
      _state.value = UiState.Loading

      runCatching {
        val fonts = googleWebFontClient.listWebFonts()

        fonts.filter {
          it.subsets.any { subset ->
            availableSubsets.contains(subset)
          }
        }
      }.onSuccess { fonts ->
        _state.value = UiState.Loaded(fonts)
      }.onException { e ->
        _state.value = UiState.Error(e)
      }
    }
  }
}
