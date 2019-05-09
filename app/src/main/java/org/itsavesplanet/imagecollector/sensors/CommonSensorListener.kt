package org.itsavesplanet.imagecollector.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.itsavesplanet.imagecollector.writers.JsonFloatArrayFileWriter
import java.io.File


open class CommonSensorListener(sensorManager: SensorManager, context: Context, file: File) : SensorEventListener {
    private val mSensorManager = sensorManager

    private val sensor: Sensor
    private var mLastT: Long = 0

    private val fileWriter = JsonFloatArrayFileWriter(file)


    private var simInProgress = false

    fun updateTimer() {
        mLastT = System.currentTimeMillis()
    }


    open fun sensorType(): Int {
        return 0
    }

    fun startSimulation() {
        fileWriter.open()
        simInProgress = true
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stopSimulation() {
        mSensorManager.unregisterListener(this)
        simInProgress = false
        fileWriter.close()
    }

    init {
        sensor = mSensorManager.getDefaultSensor(sensorType())
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!simInProgress)
            return
        if (event.sensor.getType() !== sensorType())
            return

        updateTimer()


//        mSensorZ = event.values[2]
//        when (mDisplay.getRotation()) {
//            Surface.ROTATION_0 -> {
//                mSensorX = event.values[0]
//                mSensorY = event.values[1]
//            }
//            Surface.ROTATION_90 -> {
//                mSensorX = -event.values[1]
//                mSensorY = event.values[0]
//            }
//            Surface.ROTATION_180 -> {
//                mSensorX = -event.values[0]
//                mSensorY = -event.values[1]
//            }
//            Surface.ROTATION_270 -> {
//                mSensorX = event.values[1]
//                mSensorY = -event.values[0]
//            }
//        }
        fileWriter.write(mLastT, event.values)
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
