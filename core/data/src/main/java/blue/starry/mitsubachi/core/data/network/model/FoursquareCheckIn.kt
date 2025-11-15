package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.Photo
import blue.starry.mitsubachi.core.domain.model.Source
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class FoursquareCheckIn(
  val comments: Comments,
  val createdAt: Long, // 1760321890
  val createdBy: FoursquareUser?,
  val editableUntil: Long?, // 1760408290000
  val entities: List<Entity>?,
  val event: Event?,
  val id: String,
  val isMayor: Boolean,
  val like: Boolean,
  val likes: Likes,
  val photos: Photos,
  val posts: Posts,
  val ratedAt: Long?, // 1471153590
  val shout: String?,
  val source: Source?,
  val sticker: Sticker?,
  val timeZoneOffset: Int, // 540
  val type: String, // checkin
  val user: FoursquareUser?,
  val venue: FoursquareVenue,
  val visibility: String?, // closeFriends
  val with: List<FoursquareUser>?,
) {
  @Serializable
  data class Comments(
    val count: Int, // 0
  )

  @Serializable
  data class Entity(
    val id: String,
    val indices: List<Int>,
    val type: String, // user
  )

  @Serializable
  data class Event(
    val categories: List<Category>,
    val id: String, // 5799b63e498e308eab5aa642
    val name: String, // コミックマーケット90
  ) {
    @Serializable
    data class Category(
      val icon: CategoryIcon,
      val id: String, // 4e458e3bbd41d66894dbdecc
      val name: String, // Other
      val pluralName: String, // Other
      val primary: Boolean, // true
      val shortName: String, // Other
    )

    @Serializable
    data class CategoryIcon(
      val prefix: String, // https://ss3.4sqi.net/img/categories_v2/event/default_
      val suffix: String, // .png
    )
  }

  @Serializable
  data class Likes(
    val count: Int, // 0
    val groups: List<Group>,
    val summary: String?,
  ) {
    @Serializable
    data class Group(
      val count: Int, // 1
      val items: List<FoursquareUser>,
      val type: String, // others
    )
  }

  @Serializable
  data class Photos(
    val count: Int, // 0
    val items: List<Item>,
    val layout: Layout?,
  ) {
    @Serializable
    data class Item(
      val createdAt: Int, // 1760315077
      val height: Int, // 1440
      val id: String,
      val prefix: String,
      val source: Source?,
      val suffix: String,
      val user: FoursquareUser,
      val visibility: String, // public
      val width: Int, // 1920
    )

    @Serializable
    data class Layout(
      val name: String, // single
    )
  }

  @Serializable
  data class Posts(
    val count: Int, // 0
    val textCount: Int, // 0
  )

  @Serializable
  data class Source(
    val name: String, // Swarm for iOS
    val url: String? = null, // https://www.swarmapp.com
  )

  @Serializable
  data class Sticker(
    val bonusStatus: String?, // Use once per week. Recharges Sunday at midnight.
    val bonusText: String?, // Use at Ramen Restaurant, Soba Restaurants, Udon Restaurants, Noodle Houses for a bonus.
    val group: Group,
    val id: String, // 57fbaacc498e4805f8ea2971
    val image: Image,
    val name: String, // Mr. Noodles
    val pickerPosition: PickerPosition,
    val points: Int?, // 2
    val stickerType: String, // unlockable
    val teaseText: String?, // Check in at noodle restaurants to unlock this sticker.
    val unlockText: String?, // Udon need to hear it from us. Ramen is pho-nomenal….we’ll let you stew on that one. #souppuns
  ) {
    @Serializable
    data class Group(
      val index: Int, // 72
      val name: String, // collectible
    )

    @Serializable
    data class Image(
      val name: String, // /ramen_bb433a.png
      val prefix: String, // https://fastly.4sqi.net/img/sticker/
      val sizes: List<Int>,
    )

    @Serializable
    data class PickerPosition(
      val index: Int, // 0
      val page: Int, // 3
    )
  }
}

fun FoursquareCheckIn.toDomain(): CheckIn {
  return CheckIn(
    id = id,
    venue = venue.toDomain(),
    user = user?.toDomain(),
    coin = 0, // TODO: API で取得できない？
    sticker = null,
    message = shout,
    photos = photos.items.map { it.toDomain() },
    timestamp = Instant.ofEpochSecond(createdAt).atZone(ZoneId.systemDefault()),
    isLiked = like,
    likeCount = likes.count,
    source = source?.toDomain(),
    isMeyer = isMayor,
  )
}

fun FoursquareCheckIn.Photos.Item.toDomain(size: String = "original"): Photo {
  return Photo(
    id = id,
    // https://docs.foursquare.com/developer/reference/places-photos-guide
    url = "${prefix}${size}$suffix",
    width = width,
    height = height,
  )
}

fun FoursquareCheckIn.Source.toDomain(): Source {
  return Source(
    name = name,
    url = url,
  )
}
