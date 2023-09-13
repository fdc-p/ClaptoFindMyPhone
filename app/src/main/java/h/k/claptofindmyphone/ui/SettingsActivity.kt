package h.k.claptofindmyphone.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import com.google.gson.Gson
import h.k.claptofindmyphone.R
import h.k.claptofindmyphone.databinding.ActivitySettingsBinding
import h.k.claptofindmyphone.services.RecordAudioProxy
import h.k.claptofindmyphone.services.ServiceRecordAudio

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var selectedItem = 0
    lateinit var gson: Gson

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()

        gson = Gson()

        loadWhistleSetting()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickListeners() {
        binding.toolbarClap.setOnClickListener {
            selectedItem = 1
            binding.toolbarClap.backgroundTintList =
                AppCompatResources.getColorStateList(this, R.color.app_purple)
            binding.toolbarWhistle.backgroundTintList =
                AppCompatResources.getColorStateList(this, R.color.app_greenish)
            loadClapSetting()

        }
        binding.toolbarWhistle.setOnClickListener {
            selectedItem = 0
            binding.toolbarClap.backgroundTintList =
                AppCompatResources.getColorStateList(this, R.color.app_greenish)
            binding.toolbarWhistle.backgroundTintList =
                AppCompatResources.getColorStateList(this, R.color.app_purple)
            loadWhistleSetting()

        }


        binding.switchFlash.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (selectedItem == 1) {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("flash_clap", true)
                    binding.btnFlash.setImageDrawable(resources.getDrawable(R.drawable.flash_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("flash_clap", false)
                    binding.btnFlash.setImageDrawable(resources.getDrawable(R.drawable.flash_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            } else {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("flash_whistle", true)
                    binding.btnFlash.setImageDrawable(resources.getDrawable(R.drawable.flash_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("flash_whistle", false)
                    binding.btnFlash.setImageDrawable(resources.getDrawable(R.drawable.flash_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            }
        }

        binding.switchVibrate.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (selectedItem == 1) {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("vibrate_clap", true)
                    binding.btnVibrate.setImageDrawable(resources.getDrawable(R.drawable.vibrate_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("vibrate_clap", false)
                    binding.btnVibrate.setImageDrawable(resources.getDrawable(R.drawable.vibrate_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            } else {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("vibrate_whistle", true)
                    binding.btnVibrate.setImageDrawable(resources.getDrawable(R.drawable.vibrate_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("vibrate_whistle", false)
                    binding.btnVibrate.setImageDrawable(resources.getDrawable(R.drawable.vibrate_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            }
        }

        binding.switchMelody.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (selectedItem == 1) {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("melody_clap", true)
                    binding.btnMelody.setImageDrawable(resources.getDrawable(R.drawable.melody_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("melody_clap", false)
                    binding.btnMelody.setImageDrawable(resources.getDrawable(R.drawable.melody_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            } else {
                if (isChecked) {
                    RecordAudioProxy.putBoolean("melody_whistle", true)
                    binding.btnMelody.setImageDrawable(resources.getDrawable(R.drawable.melody_on_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                } else {
                    RecordAudioProxy.putBoolean("melody_whistle", false)
                    binding.btnMelody.setImageDrawable(resources.getDrawable(R.drawable.melody_off_btn))
                    if (isServiceRunning(ServiceRecordAudio::class.java)) {
                        val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                        startForegroundService(i)
                    }
                }
            }
        }

        binding.btnFlash.setOnClickListener {
            binding.switchFlash.isChecked = !binding.switchFlash.isChecked
        }
        binding.btnVibrate.setOnClickListener {
            binding.switchVibrate.isChecked = !binding.switchVibrate.isChecked
        }
        binding.btnMelody.setOnClickListener {
            binding.switchMelody.isChecked = !binding.switchMelody.isChecked
        }
        binding.lengthSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var prog = p1
                if (prog == 0) prog = 1
                if (selectedItem == 1) {
                    RecordAudioProxy.putInt("melody_length_clap", prog * 50)
                } else {
                    RecordAudioProxy.putInt("melody_length_whistle", prog * 50)
                }
                if (isServiceRunning(ServiceRecordAudio::class.java)) {
                    val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                    startForegroundService(i)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStopTrackingTouch(p0: SeekBar?) {
                var prog = p0?.progress
                if (prog == 0) prog = 1
                if (selectedItem == 1) {
                    RecordAudioProxy.putInt("melody_length_clap", prog?.times(50) ?: 500)
                } else {
                    RecordAudioProxy.putInt("melody_length_whistle", prog?.times(50) ?: 500)
                }
                if (isServiceRunning(ServiceRecordAudio::class.java)) {
                    val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                    startForegroundService(i)
                }
            }

        })


        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var prog = p1
                if (prog == 0) prog = 1
                if (selectedItem == 1) {
                    RecordAudioProxy.putInt("melody_volume_clap", prog)
                } else {
                    RecordAudioProxy.putInt("melody_volume_whistle", prog)
                }
                if (isServiceRunning(ServiceRecordAudio::class.java)) {
                    val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                    startForegroundService(i)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStopTrackingTouch(p0: SeekBar?) {
                var prog = p0?.progress
                if (prog == 0) prog = 1
                if (selectedItem == 1) {
                    RecordAudioProxy.putInt("melody_volume_clap", prog ?: 100)
                } else {
                    RecordAudioProxy.putInt("melody_volume_whistle", prog ?: 100)
                }
                if (isServiceRunning(ServiceRecordAudio::class.java)) {
                    val i = Intent(this@SettingsActivity, ServiceRecordAudio::class.java)
                    startForegroundService(i)
                }
            }

        })


    }

    private fun loadClapSetting() {
        binding.switchFlash.isChecked = RecordAudioProxy.getBoolean("flash_clap", false)
        binding.switchVibrate.isChecked = RecordAudioProxy.getBoolean("vibrate_clap", false)
        binding.switchMelody.isChecked = RecordAudioProxy.getBoolean("melody_clap", false)
        binding.lengthSeekbar.progress = RecordAudioProxy.getInt("melody_length_clap", 500) / 50
        binding.volumeSeekbar.progress = RecordAudioProxy.getInt("melody_volume_clap", 100)
    }

    private fun loadWhistleSetting() {
        binding.switchFlash.isChecked = RecordAudioProxy.getBoolean("flash_whistle", false)
        binding.switchVibrate.isChecked = RecordAudioProxy.getBoolean("vibrate_whistle", false)
        binding.switchMelody.isChecked = RecordAudioProxy.getBoolean("melody_whistle", false)
        binding.lengthSeekbar.progress = RecordAudioProxy.getInt("melody_length_whistle", 500) / 50
        binding.volumeSeekbar.progress = RecordAudioProxy.getInt("melody_volume_whistle", 100)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}