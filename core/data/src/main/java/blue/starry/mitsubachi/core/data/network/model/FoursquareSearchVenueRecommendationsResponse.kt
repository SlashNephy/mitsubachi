package blue.starry.mitsubachi.core.data.network.model

import blue.starry.mitsubachi.core.domain.model.VenueRecommendation
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareSearchVenueRecommendationsResponse(
  val context: Context,
  val group: Group,
) {
  @Serializable
  data class Context(
    val boundsSummaryRadius: Int, // 1435
    val currency: String, // ¥
    val currentLocation: CurrentLocation,
    val geoBounds: GeoBounds,
    val geoParams: GeoParams,
    val searchLocationMapBounds: Boolean, // false
    val searchLocationNearYou: Boolean, // true
    val searchLocationType: String, // NearYou
  ) {
    @Serializable
    data class CurrentLocation(
      val feature: Feature,
      val what: String,
      val where: String,
    ) {
      @Serializable
      data class Feature(
        val cc: String, // JP
        val displayName: String, // 品川区, 東京, 東京都
        val encumbered: Boolean, // true
        val geometry: Geometry,
        val id: String, // maponics:10004320
        val longId: String, // 10004320
        val name: String, // 品川区
        val slug: String?, // shinagawa-tokyo-to-japan
        val woeType: Int, // 22
      ) {
        @Serializable
        data class Geometry(
          val bounds: Bounds,
          val center: Center,
        ) {
          @Serializable
          data class Bounds(
            val ne: Ne,
            val sw: Sw,
          ) {
            @Serializable
            data class Ne(
              val lat: Double, // 35.64321
              val lng: Double, // 139.7694212
            )

            @Serializable
            data class Sw(
              val lat: Double, // 35.58301
              val lng: Double, // 139.693
            )
          }

          @Serializable
          data class Center(
            val lat: Double, // 35.610254
            val lng: Double, // 139.733162
          )
        }
      }
    }

    @Serializable
    data class GeoBounds(
      val circle: Circle,
    ) {
      @Serializable
      data class Circle(
        val center: Center,
        val radius: Int, // 1435
      ) {
        @Serializable
        data class Center(
          val lat: Double, // 35.610184
          val lng: Double, // 139.748544
        )
      }
    }

    @Serializable
    data class GeoParams(
      val ll: String, // 35.610184,139.748544
      val radius: String, // 1435
    )
  }

  @Serializable
  data class Group(
    val results: List<Result>?,
    val totalResults: Int, // 194
  ) {
    @Serializable
    data class Result(
      val displayType: String, // venue
      val id: String, // 69060976c9e2c646608e1620
      val photo: Photo?,
      val photos: Photos,
      val snippets: Snippets,
      val venue: FoursquareVenue,
    ) {
      @Serializable
      data class Photo(
        val createdAt: Int, // 1696946115
        val height: Int, // 1439
        val id: String, // 652557c320fbbb7de30b9ec6
        val prefix: String, // https://fastly.4sqi.net/img/general/
        val suffix: String, // /550961_PhRJ5xyOaE7mCVEDkGQPYZMla4o--Fdmvqw54c2i9NQ.jpg
        val visibility: String, // public
        val width: Int, // 1440
      )

      @Serializable
      data class Photos(
        val count: Int, // 1
        val groups: List<Group>,
      ) {
        @Serializable
        data class Group(
          val count: Int, // 1
          val items: List<Item>,
          val name: String, // スポットの写真
          val type: String, // venue
        ) {
          @Serializable
          data class Item(
            val createdAt: Int, // 1696946115
            val height: Int, // 1439
            val id: String, // 652557c320fbbb7de30b9ec6
            val prefix: String, // https://fastly.4sqi.net/img/general/
            val suffix: String, // /550961_PhRJ5xyOaE7mCVEDkGQPYZMla4o--Fdmvqw54c2i9NQ.jpg
            val user: FoursquareUser,
            val visibility: String, // public
            val width: Int, // 1440
          )
        }
      }

      @Serializable
      data class Snippets(
        val count: Int, // 2
        val items: List<Item>,
      ) {
        @Serializable
        data class Item(
          val detail: Detail?,
        ) {
          @Serializable
          data class Detail(
            val `object`: Object,
            val type: String, // socialStatusBar
          ) {
            @Serializable
            data class Object(
              val agreeCount: Int?, // 2
              val canonicalUrl: String?, // https://app.foursquare.com/item/5b87a09295d986002c4e2587
              val createdAt: Int?, // 1535615122
              val disagreeCount: Int?, // 0
              val editedAt: Int?, // 1614434061
              val entities: List<Entity>?,
              val id: String?, // 5b87a09295d986002c4e2587
              val likes: Likes?,
              val logView: Boolean?, // true
              val socialStatusBar: SocialStatusBar?,
              val text: String?, // ドリンクが充実してますが、まいばすけっととしては少し広めで、店内でアルコール以外は食事可能です。
              val todo: Todo?,
              val type: String?, // user
              val url: String?, // https://api.foursquare.com/v2/tips/add
              val user: FoursquareUser?,
              val viewCount: Int?, // 659
            ) {
              @Serializable
              data class Entity(
                val indices: List<Int>,
                val `object`: Object?,
                val type: String, // url
              ) {
                @Serializable
                data class Object(
                  val url: String, // http://giftee.co/store/tyharbor
                )
              }

              @Serializable
              data class Likes(
                val count: Int, // 1
                val summary: String, // 1 人がお気に入り
              )

              @Serializable
              data class SocialStatusBar(
                val items: List<Item>,
              ) {
                @Serializable
                data class Item(
                  val topRated: Boolean?, // true
                  val type: String, // topRatedState
                )
              }

              @Serializable
              data class Todo(
                val count: Int, // 0
              )
            }
          }
        }
      }
    }
  }
}

fun FoursquareSearchVenueRecommendationsResponse.Group.Result.toDomain(): VenueRecommendation {
  return VenueRecommendation(
    id = id,
    venue = venue.toDomain(),
    photo = photo?.let {
      VenueRecommendation.Photo(
        id = it.id,
        prefix = it.prefix,
        suffix = it.suffix,
        width = it.width,
        height = it.height,
      )
    },
    tips = snippets.items.mapNotNull { item ->
      item.detail?.`object`?.let { obj ->
        obj.text?.let { text ->
          VenueRecommendation.Tip(
            id = obj.id ?: "",
            text = text,
            userName = obj.user?.firstName,
          )
        }
      }
    },
  )
}
