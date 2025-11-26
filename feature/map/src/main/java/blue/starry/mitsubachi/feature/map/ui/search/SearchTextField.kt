package blue.starry.mitsubachi.feature.map.ui.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.ui.symbols.MaterialSymbols
import blue.starry.mitsubachi.feature.map.R

@Composable
internal fun SearchTextField(
  query: String,
  onChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }

  BasicTextField(
    value = query,
    onValueChange = onChange,
    singleLine = true,
    maxLines = 1,
    decorationBox = {
      TextFieldDefaults.DecorationBox(
        value = query,
        visualTransformation = VisualTransformation.None,
        innerTextField = it,
        enabled = true,
        singleLine = true,
        interactionSource = interactionSource,
        placeholder = { Text(stringResource(R.string.search_map_search_hint)) },
        leadingIcon = {
          Icon(painterResource(MaterialSymbols.search), contentDescription = null)
        },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { onChange("") }) {
              Icon(painterResource(MaterialSymbols.clear), contentDescription = null)
            }
          }
        },
        shape = ShapeDefaults.ExtraLarge,
        colors = TextFieldDefaults.colors(
          unfocusedIndicatorColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
          focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        contentPadding = PaddingValues(8.dp),
      )
    },
    modifier = modifier.heightIn(max = 48.dp),
  )
}

@Preview
@Composable
private fun SearchTextFieldPreview() {
  SearchTextField(
    query = "",
    onChange = {},
  )
}
