package org.itsavesplanet.imagecollector.sensors

import android.annotation.TargetApi
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import org.itsavesplanet.imagecollector.writers.JsonFloatArrayFileWriter
import java.io.File

//https://github.com/googlesamples/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

internal class Accelerometer(sensorManager: SensorManager, context: Context, file: File) : SensorEventListener {
    // diameter of the balls in meters

    private val mSensorManager = sensorManager

    private val mAccelerometer: Sensor
    private var mLastT: Long = 0

    private val fileWriter = JsonFloatArrayFileWriter(file)

    private var mXOrigin: Float = 0.toFloat()
    private var mYOrigin: Float = 0.toFloat()
    private var mZOrigin: Float = 0.toFloat()
    private var mSensorX: Float = 0.toFloat()
    private var mSensorY: Float = 0.toFloat()
    private var mSensorZ: Float = 0.toFloat()
    private var mHorizontalBound: Float = 0.toFloat()
    private var mVerticalBound: Float = 0.toFloat()

    private var simInProgress = false

    fun updateTimer() {
        mLastT = System.currentTimeMillis()
    }


    fun startSimulation() {
        //         todo: save initial sensor values
        /*
         * It is not necessary to get accelerometer events at a very high
         * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
         * automatic low-pass filter, which "extracts" the gravity component
         * of the acceleration. As an added benefit, we use less power and
         * CPU resources.
         */
        fileWriter.open()
        simInProgress = true
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stopSimulation() {
        mSensorManager.unregisterListener(this)
        simInProgress = false
        fileWriter.close()
    }

    init {
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!simInProgress)
            return
        if (event.sensor.getType() !== Sensor.TYPE_ACCELEROMETER)
            return

        updateTimer()

        /*
         * record the accelerometer data, the event's timestamp as well as
         * the current time. The latter is needed so we can calculate the
         * "present" time during rendering. In this application, we need to
         * take into account how the screen is rotated with respect to the
         * sensors (which always return data in a coordinate space aligned
         * to with the screen in its native orientation).
         */

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
