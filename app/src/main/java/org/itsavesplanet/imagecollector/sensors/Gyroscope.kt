package org.itsavesplanet.imagecollector.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.itsavesplanet.imagecollector.writers.JsonFloatArrayFileWriter
import java.io.File


internal class Gyroscope(sensorManager: SensorManager, context: Context, file: File) :
    CommonSensorListener(sensorManager, context, file) {

    override fun sensorType(): Int {
        return Sensor.TYPE_GYROSCOPE
    }
}
