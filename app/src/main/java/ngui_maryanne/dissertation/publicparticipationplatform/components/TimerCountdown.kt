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

        when {
            duration.isNegative -> "Expired"
            duration.toHours() > 0 -> "${duration.toHours()}h ${duration.toMinutesPart()}m"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}m"
            else -> "${duration.seconds}s"
        }
    } catch (e: Exception) {
        "Invalid time"
    }
}
