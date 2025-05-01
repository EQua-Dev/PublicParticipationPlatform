package ngui_maryanne.dissertation.publicparticipationplatform.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.Duration
import java.time.Instant
@RequiresApi(Build.VERSION_CODES.O)
fun getTimeLeft(expiryTimeMillis: Long): String {
    return try {
        val expiry = Instant.ofEpochMilli(expiryTimeMillis)
        val now = Instant.now()
        val duration = Duration.between(now, expiry)

        if (duration.isNegative) {
            "Expired"
        } else {
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60

            when {
                hours > 0 -> "${hours}h ${minutes}m"
                duration.toMinutes() > 0 -> "${duration.toMinutes()}m"
                else -> "${seconds}s"
            }
        }
    } catch (e: Exception) {
        "Invalid time"
    }
}
