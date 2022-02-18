package com.example.trackingapp.sensor.androidsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.SensorAccuracy
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class OrientationSensor : AbstractSensor(
    "ORIENTATION_SENSOR",
    "Orientation"
), SensorEventListener {

    private var sensorManager: SensorManager? = null

    override fun isAvailable(context: Context): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Log.d(TAG, "is Sensor available: ${sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null}")
        return sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!m_isSensorAvailable) return
        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            sensorManager?.unregisterListener(this)
            isRunning = false
        }
    }

    fun saveEntry(timestamp: Long, sensorData: String, accuracy: String) {
        LogEvent(LogEventName.PHONE_ORIENTATION, timestamp, sensorData, accuracy).saveToDataBase()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

   // private var cachedOrientation: FloatArray = floatArrayOf(0f,0f,0f)

    override fun onSensorChanged(event: SensorEvent?) {
        val time = System.currentTimeMillis()
        if (isRunning && LoggingManager.userPresent && event != null) {
            try {
           //     Log.d("xxx","sensor orienteion: ${cachedOrientation.contentEquals(event.values)}")
          //      Log.d("xxx"," sensoreven7: ${CONST.numberFormat.format(event.values[0])}, ${CONST.numberFormat.format(event.values[1])},${CONST.numberFormat.format(event.values[2])}")
         //       Log.d("xxx"," sensorchached: ${CONST.numberFormat.format(cachedOrientation[0])}, ${CONST.numberFormat.format(cachedOrientation[1])},${CONST.numberFormat.format(cachedOrientation[2])}")
               // if (!cachedOrientation.contentEquals(event.values)){
                    when (event.accuracy) {
                        SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                          //  cachedOrientation = event.values
                            val sensorData = "${CONST.numberFormat.format(event.values[0])}, " +
                                    "${CONST.numberFormat.format(event.values[1])},${CONST.numberFormat.format(event.values[2])}"
                            saveEntry(time, sensorData, SensorAccuracy.ACCURACY_UNRELAIABLE.name)
                        }
                        else -> {
                           // cachedOrientation = event.values
                            val sensorData = "${CONST.numberFormat.format(event.values?.get(0))}, " +
                                    "${CONST.numberFormat.format(event.values?.get(1))}, ${CONST.numberFormat.format(event.values?.get(2))}"
                            saveEntry(time, sensorData, SensorAccuracy.ACCURACY_ELSE.name)
                        }
                    }
             //   }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

}