package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.GoogleWebFont

interface GoogleWebFontClient {
  suspend fun listWebFonts(): List<GoogleWebFont>
}
