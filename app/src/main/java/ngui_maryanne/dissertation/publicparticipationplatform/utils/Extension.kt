package ngui_maryanne.dissertation.publicparticipationplatform.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import java.util.Calendar
import kotlin.math.abs


//toast function
fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun openWhatsapp(phoneNumber: String, context: Context) {

    val pm = context.packageManager
    val waIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
    )
    val info = pm.queryIntentActivities(waIntent, 0)
    if (info.isNotEmpty()) {
        context.startActivity(waIntent)
    } else {
        context.toast("WhatsApp not Installed")
    }
}

fun openDial(phoneNumber: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    context.startActivity(intent)
}




fun calculateDaysDifference(startDate: Calendar, endDate: Calendar): Int {
    val startTime = startDate.timeInMillis
    val endTime = endDate.timeInMillis
    val differenceInMillis = abs(endTime - startTime)
    val differenceInDays = (differenceInMillis / (1000 * 60 * 60 * 24)).toInt()
    return differenceInDays
}

// Helper extension function
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}
