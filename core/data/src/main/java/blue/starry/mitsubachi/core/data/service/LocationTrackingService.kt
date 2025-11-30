package blue.starry.mitsubachi.core.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import blue.starry.mitsubachi.core.data.repository.model.toDomain
import blue.starry.mitsubachi.core.domain.model.Coordinates
import blue.starry.mitsubachi.core.domain.model.DeviceLocation
import blue.starry.mitsubachi.core.domain.usecase.FoursquareApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
@Suppress("TooManyFunctions")
class LocationTrackingService : Service() {
  @Suppress("LateinitUsage")
  @Inject
  lateinit var fusedLocationClient: FusedLocationProviderClient

  @Suppress("LateinitUsage")
  @Inject
  lateinit var foursquareApiClient: FoursquareApiClient

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  private val locationExecutor = Executors.newSingleThreadExecutor()

  @Suppress("LateinitUsage")
  private lateinit var notificationManager: NotificationManager

  private var currentLocation: DeviceLocation? = null
  private var stayStartTime: Long = 0
  private var lastNotificationTime: Long = 0

  private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(result: LocationResult) {
      result.lastLocation?.let { location ->
        val newLocation = location.toDomain()
        handleLocationUpdate(newLocation)
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannels()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_START_TRACKING -> startLocationTracking()
      ACTION_STOP_TRACKING -> stopLocationTracking()
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onDestroy() {
    super.onDestroy()
    stopLocationTracking()
    locationExecutor.shutdown()
    serviceScope.cancel()
  }

  private fun createNotificationChannels() {
    val serviceChannel = NotificationChannel(
      NOTIFICATION_CHANNEL_SERVICE,
      "Location Tracking Service",
      NotificationManager.IMPORTANCE_LOW,
    ).apply {
      description = "Ongoing location tracking notification"
      setShowBadge(false)
    }

    val checkInChannel = NotificationChannel(
      NOTIFICATION_CHANNEL_CHECKIN,
      "Check-in Reminders",
      NotificationManager.IMPORTANCE_HIGH,
    ).apply {
      description = "Notifications to prompt venue check-ins"
      setShowBadge(true)
    }

    notificationManager.createNotificationChannel(serviceChannel)
    notificationManager.createNotificationChannel(checkInChannel)
  }

  private fun startLocationTracking() {
    val notification = createServiceNotification()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(
        NOTIFICATION_ID_SERVICE,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
      )
    } else {
      startForeground(NOTIFICATION_ID_SERVICE, notification)
    }

    try {
      val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        LOCATION_UPDATE_INTERVAL_MS,
      ).apply {
        setMinUpdateIntervalMillis(LOCATION_UPDATE_FASTEST_INTERVAL_MS)
        setWaitForAccurateLocation(false)
      }.build()

      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationExecutor,
        locationCallback,
      )

      Timber.d("Location tracking started")
    } catch (e: SecurityException) {
      Timber.e(e, "Failed to start location tracking: missing permission")
      stopSelf()
    }
  }

  private fun stopLocationTracking() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
    Timber.d("Location tracking stopped")
  }

  private fun createServiceNotification(): Notification {
    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_SERVICE)
      .setContentTitle("Location Tracking Active")
      .setContentText("Monitoring your location for venue check-ins")
      .setSmallIcon(android.R.drawable.ic_menu_mylocation)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setOngoing(true)
      .build()
  }

  private fun handleLocationUpdate(newLocation: DeviceLocation) {
    val previousLocation = currentLocation

    if (previousLocation == null) {
      // First location update
      currentLocation = newLocation
      stayStartTime = System.currentTimeMillis()
      return
    }

    val distanceMeters = calculateDistance(
      previousLocation.latitude,
      previousLocation.longitude,
      newLocation.latitude,
      newLocation.longitude,
    )

    if (distanceMeters < STAY_RADIUS_METERS) {
      // User is still at the same location
      val stayDuration = System.currentTimeMillis() - stayStartTime

      if (stayDuration >= STAY_DURATION_THRESHOLD_MS) {
        val timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTime

        if (timeSinceLastNotification >= NOTIFICATION_COOLDOWN_MS) {
          // User has been at this location long enough, check for nearby venues
          checkNearbyVenuesAndNotify(newLocation)
        }
      }
    } else {
      // User has moved to a new location
      currentLocation = newLocation
      stayStartTime = System.currentTimeMillis()
    }
  }

  private fun checkNearbyVenuesAndNotify(location: DeviceLocation) {
    serviceScope.launch {
      @Suppress("TooGenericExceptionCaught")
      try {
        val coordinates = Coordinates(location.latitude, location.longitude)
        val venues = foursquareApiClient.searchNearVenues(coordinates, query = null)

        if (venues.isNotEmpty()) {
          val venue = venues.first()
          sendCheckInNotification(venue.name)
          lastNotificationTime = System.currentTimeMillis()
        }
      } catch (e: Exception) {
        Timber.e(e, "Failed to fetch nearby venues")
      }
    }
  }

  private fun sendCheckInNotification(venueName: String) {
    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_CHECKIN)
      .setContentTitle("Check in at $venueName?")
      .setContentText("You've been here for a while. Tap to check in.")
      .setSmallIcon(android.R.drawable.ic_dialog_map)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(NOTIFICATION_ID_CHECKIN, notification)
    Timber.d("Check-in notification sent for venue: $venueName")
  }

  private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = (lat2 - lat1) * PI / 180
    val dLon = (lon2 - lon1) * PI / 180

    val a = sin(dLat / 2).pow(2) +
      cos(lat1 * PI / 180) * cos(lat2 * PI / 180) *
      sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c * 1000 // Convert to meters
  }

  companion object {
    private const val ACTION_START_TRACKING = "blue.starry.mitsubachi.action.START_TRACKING"
    private const val ACTION_STOP_TRACKING = "blue.starry.mitsubachi.action.STOP_TRACKING"

    private const val NOTIFICATION_CHANNEL_SERVICE = "location_tracking_service"
    private const val NOTIFICATION_CHANNEL_CHECKIN = "check_in_reminder"
    private const val NOTIFICATION_ID_SERVICE = 1001
    private const val NOTIFICATION_ID_CHECKIN = 1002

    private const val LOCATION_UPDATE_INTERVAL_MS = 30_000L // 30 seconds
    private const val LOCATION_UPDATE_FASTEST_INTERVAL_MS = 15_000L // 15 seconds
    private const val STAY_RADIUS_METERS = 100.0 // 100 meters
    private const val STAY_DURATION_THRESHOLD_MS = 5 * 60 * 1000L // 5 minutes
    private const val NOTIFICATION_COOLDOWN_MS = 30 * 60 * 1000L // 30 minutes

    fun startTracking(context: Context) {
      val intent = Intent(context, LocationTrackingService::class.java).apply {
        action = ACTION_START_TRACKING
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopTracking(context: Context) {
      val intent = Intent(context, LocationTrackingService::class.java).apply {
        action = ACTION_STOP_TRACKING
      }
      context.startService(intent)
    }
  }
}
