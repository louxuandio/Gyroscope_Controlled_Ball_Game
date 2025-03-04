package com.example.gyroscopecontrolledballgame

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gyroscopecontrolledballgame.ui.theme.GyroscopeControlledBallGameTheme

class MainActivity : ComponentActivity(), SensorEventListener  {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var gyro_xyz = FloatArray(3)

    private var pitch by mutableStateOf(0f)
    private var roll by mutableStateOf(0f)

    private var previousPitch = 0f
    private var previousRoll = 0f
    private var lastTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            GyroscopeControlledBallGameTheme {
                Surface {  }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        gyroscope?.let{
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            gyro_xyz[0] = it.values[0]
            gyro_xyz[1] = it.values[1]
            gyro_xyz[2] = it.values[2]

            if (lastTimestamp!=0L){
                val dt = (it.timestamp - lastTimestamp) / 1_000_000_000f
                val newPitchRate = it.values[0]
                val newRollRate = it.values[1]
                val newPitch = previousPitch + newPitchRate * dt * (180f/Math.PI.toFloat())
                val newRoll = previousRoll + newRollRate * dt * (180f/Math.PI.toFloat())
                pitch = newPitch
                roll = newRoll
                previousRoll = newRoll
                previousPitch = newPitch
            }
            lastTimestamp = it.timestamp
        }
        // update ball state
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GyroscopeControlledBallGameTheme {
        Greeting("Android")
    }
}