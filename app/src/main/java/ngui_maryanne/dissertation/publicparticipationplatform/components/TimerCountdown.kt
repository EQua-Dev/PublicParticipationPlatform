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
            val totalSeconds = duration.seconds

            val days = totalSeconds / (24 * 3600)
            val hours = (totalSeconds % (24 * 3600)) / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            when {
                days > 0 -> "${days}d ${hours}h ${minutes}m"
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m"
                else -> "${seconds}s"
            }
        }
    } catch (e: Exception) {
        "Invalid time"
    }
}
