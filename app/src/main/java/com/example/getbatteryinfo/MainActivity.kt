package com.example.getbatteryinfo

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.getbatteryinfo.ui.theme.GetBatteryInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetBatteryInfoTheme {
                Surface {
                    BatteryView()
                }
            }
        }
    }
}

@Composable
fun BatteryView() {
    val context = LocalContext.current

    val paddingState: MutableState<Dp> = remember { mutableStateOf(8.dp) }
    val batteryPer: MutableState<Float> = rememberSaveable {
        mutableFloatStateOf(0.0F)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Must Show All Information from Battery Manager")
        Text(
            modifier = Modifier.padding(paddingState.value),
            text = "Percentage : " + batteryPer.value.toString()
        )
        Button(onClick = {
            batteryPer.value = getBattery(context = context)
//            paddingState.value += 8.dp
        }) {
            Text("Get Battery")
        }
    }
}

fun getBattery(context: Context): Float {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL

    // How are we charging?
    val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
    val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
    val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

    val batteryPct: Float? = batteryStatus?.let { intent ->
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        level * 100 / scale.toFloat()
    }
    return batteryPct ?: 0.0F
}

@Preview(
    name = "dark mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(name = "light mode", showBackground = true)
@Composable
private fun HomePage() {
    GetBatteryInfoTheme {
        Surface {
            BatteryView()
        }
    }
}