package org.itsavesplanet.imagecollector.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import java.io.File

//https://github.com/googlesamples/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

internal class LinearAccelerometer(sensorManager: SensorManager, context: Context, file: File) :
    CommonSensorListener(sensorManager, context, file) {

    override fun sensorType(): Int {
        return Sensor.TYPE_LINEAR_ACCELERATION
    }
}
