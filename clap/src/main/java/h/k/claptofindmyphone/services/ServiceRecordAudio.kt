package h.k.claptofindmyphone.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.media.*
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import h.k.claptofindmyphone.R
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*


class ServiceRecordAudio : Service() {
    private var mNM: NotificationManager? = null
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var classificationInterval = 500L // how often should classification run in milli-secs
    private lateinit var handler: Handler // background thread handler to run classification
    lateinit var gson: Gson
    private var winManager: WindowManager? = null
    private var winParam: WindowManager.LayoutParams? = null
    private var touchIcon: View? = null
    private var displayMetrics: DisplayMetrics? = null
    lateinit var vibrator: Vibrator
    private lateinit var ringtone: Ringtone
    lateinit var cameraManager: CameraManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        var notiIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notiIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, RecordAudioProxy.serverCls),
                    PendingIntent.FLAG_MUTABLE
                )
        } else {
            notiIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, RecordAudioProxy.serverCls),
                    PendingIntent.FLAG_ONE_SHOT
                )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                "1",
                "Clap To Find My Phone", NotificationManager.IMPORTANCE_NONE
            )
            mNM?.createNotificationChannel(chan)
        } else {
            ""
        }
        val notification: Notification = Notification.Builder(this, "1")
            .setSmallIcon(R.drawable.clap) // the status icon
            .setWhen(System.currentTimeMillis()) // the time stamp
            .setContentTitle("Clap To Find My Phone") // the label of the entry
            .setContentIntent(notiIntent) // The intent to send when the entry is clicked
            .build()
        startForeground(159, notification)

        displayMetrics = DisplayMetrics()
        touchIcon = LayoutInflater.from(this).inflate(R.layout.activity_alarm, null)
        winManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        winParam = WindowManager.LayoutParams()
        winManager?.defaultDisplay?.getMetrics(displayMetrics)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            winParam!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            winParam!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }

        winParam!!.width = WindowManager.LayoutParams.MATCH_PARENT
        winParam!!.height = WindowManager.LayoutParams.MATCH_PARENT
        winParam!!.x = displayMetrics!!.widthPixels / 2
        winParam!!.y = displayMetrics!!.widthPixels / 1
        winParam!!.gravity = (Gravity.TOP or Gravity.LEFT)
        winParam!!.format = PixelFormat.RGBA_8888
        winParam!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        vibrator=this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        ringtone = RingtoneManager.getRingtone(applicationContext, RingtoneManager.getActualDefaultRingtoneUri(this,
            RingtoneManager.TYPE_RINGTONE))

        gson = Gson()

        handler = Handler()
        touchIcon!!.findViewById<View>(R.id.animationView).setOnClickListener {
            ringtone.stop()
            winManager?.removeView(touchIcon)

        }
        startAudioClassification()
        //////


    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mNM?.cancel(159)
        winManager?.removeView(touchIcon)
        handler.removeCallbacksAndMessages(null)
        audioRecord?.stop()
        audioRecord = null
        audioClassifier = null
//        Log.e("firstday",recorder!!.maxAmplitude.toString())
//        recorder!!.stop();
//        recorder!!.release();
    }

    private fun startAudioClassification() {
        // If the audio classifier is initialized and running, do nothing.
        if (audioClassifier != null) return;

        // Initialize the audio classifier
        val classifier = AudioClassifier.createFromFile(this, "cmcm.tflite")
        val audioTensor = classifier.createInputTensorAudio()

        // Initialize the audio recorder
        val record = classifier.createAudioRecord()
        record.startRecording()

        // Define the classification runnable
        val run = object : Runnable {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun run() {
                val startTime = System.currentTimeMillis()

                // Load the latest audio sample
                audioTensor.load(record)
                val output = classifier.classify(audioTensor)

                // Filter out results above a certain threshold, and sort them descendingly
                val filteredModelOutput = output[0].categories.filter {
                    it.score > 0.3f
                }.sortedBy {
                    -it.score
                }


                val finishTime = System.currentTimeMillis()
                if (filteredModelOutput.size > 0) {
                    Log.e(
                        "qq",
                        filteredModelOutput[0].label.toString() + " , " + filteredModelOutput[0].index.toString()
                    )
                    //Hands , 56
                    //Finger snapping , 57
                    //Cap gun , 425
                    if (filteredModelOutput[0].index == 56
                        || filteredModelOutput[0].index == 57
                        || filteredModelOutput[0].index == 58
                        || filteredModelOutput[0].index == 425) {

                        if (RecordAudioProxy.getBoolean("melody_clap", false)) {
                            ringtone.play()
                        }
                        if (RecordAudioProxy.getBoolean("flash_clap", false)) {
                            if (this@ServiceRecordAudio.packageManager?.hasSystemFeature(
                                    PackageManager.FEATURE_CAMERA_FLASH
                                ) == true
                            ) {

                                cameraManager = this@ServiceRecordAudio.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                                val myString = "0101010101"
                                val blinkDelay: Long = 50
                                for (i in 0 until myString.length) {
                                    if (myString[i] == '0') {
                                        cameraManager.setTorchMode(
                                            cameraManager.cameraIdList[0],
                                            true
                                        )
                                    } else {
                                        cameraManager.setTorchMode(
                                            cameraManager.cameraIdList[0],
                                            false
                                        )
                                    }
                                    try {
                                        Thread.sleep(blinkDelay)
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                        if (RecordAudioProxy.getBoolean("vibrate_clap", false)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(RecordAudioProxy.getInt("melody_length_clap",500).toLong(), VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(RecordAudioProxy.getInt("melody_length_clap",500).toLong());
                            }
                        }

                        try {
                            winManager?.addView(touchIcon, winParam)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }



                    } else if (filteredModelOutput[0].index == 426 || filteredModelOutput[0].index == 479 || filteredModelOutput[0].index == 396 || filteredModelOutput[0].index == 79 || filteredModelOutput[0].index == 35) {

                        if (RecordAudioProxy.getBoolean("melody_whistle", false)) {
                            ringtone.play()
                        }
                        if (RecordAudioProxy.getBoolean("flash_whistle", false)) {
                            if (this@ServiceRecordAudio.packageManager?.hasSystemFeature(
                                    PackageManager.FEATURE_CAMERA_FLASH
                                ) == true
                            ) {

                                cameraManager = this@ServiceRecordAudio.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                                val myString = "0101010101"
                                val blinkDelay: Long = 50
                                for (i in 0 until myString.length) {
                                    if (myString[i] == '0') {
                                        cameraManager.setTorchMode(
                                            cameraManager.cameraIdList[0],
                                            true
                                        )
                                    } else {
                                        cameraManager.setTorchMode(
                                            cameraManager.cameraIdList[0],
                                            false
                                        )
                                    }
                                    try {
                                        Thread.sleep(blinkDelay)
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                        if (RecordAudioProxy.getBoolean("vibrate_whistle", false)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(RecordAudioProxy.getInt("melody_length_clap",500).toLong(), VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(RecordAudioProxy.getInt("melody_length_clap",500).toLong());
                            }
                        }
                        try {
                            winManager?.addView(touchIcon, winParam)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }


                }
                Log.d("TAG", "Latency = ${finishTime - startTime}ms")

                // Updating the UI

                // Rerun the classification after a certain interval
                handler.postDelayed(this, classificationInterval)
            }
        }

// Start the classification process
        handler.post(run)

// Save the instances we just created for use later
        audioClassifier = classifier
        audioRecord = record
    }

}