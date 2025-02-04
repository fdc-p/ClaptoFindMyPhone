package h.k.claptofindmyphone.ui

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.Gson
import h.k.claptofindmyphone.R
import h.k.claptofindmyphone.databinding.ActivityMainBinding
import h.k.claptofindmyphone.services.RecordAudioProxy
import h.k.claptofindmyphone.services.ServiceRecordAudio


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var gson= Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RecordAudioProxy.onApplicationCreate(this, MainActivity::class.java)
        RecordAudioProxy.onConfigInit()

        if (isServiceRunning(ServiceRecordAudio::class.java)){
            binding.switchBtn.isChecked=true
        }
        animateNavigationDrawer()
        menuPoping()

        binding.switchBtn.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked){
                if (!isServiceRunning(ServiceRecordAudio::class.java)){
                    RecordAudioProxy.startForegroundService(this)
                    binding.switchText.text="Stop"
                }
            }
            else{
                RecordAudioProxy.stopService(this)
                binding.switchText.text="Start"

            }
        }


        binding.btnOnOff.setOnClickListener {
            binding.switchBtn.isChecked = !binding.switchBtn.isChecked
        }

        binding.btnSetting.setOnClickListener {

//            r.isLooping=true

//            val currentRingtone: Uri = RingtoneManager.getActualDefaultRingtoneUri(this,
//                RingtoneManager.TYPE_ALARM)
//            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
//                RingtoneManager.TYPE_RINGTONE)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtone)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
//            startActivityForResult(intent, 999)
            startActivity(Intent(this,SettingsActivity::class.java))
        }
//        binding.hisBtn.setOnClickListener {
//            binding.hisBtn.animate().rotation(360F)
//            startActivity(Intent(this,RecordActivity::class.java))
//        }
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

    private fun animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset = slideOffset * (1 - 0.7f)
                val offsetScale = 1 - diffScaledOffset
                binding.mainMen.scaleX = offsetScale
                binding.mainMen.scaleY = offsetScale

                // Translate the View, accounting for the scaled width
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff =binding.mainMen.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                binding.mainMen.translationX = xTranslation
            }
        })
    }
    private fun menuPoping() {
        binding.animationView.setOnClickListener {
//            Toast.makeText(this,"TTT",Toast.LENGTH_SHORT).show()
            if (binding.drawerLayout.isDrawerVisible(
                    GravityCompat.START
                )
            ) binding.drawerLayout.closeDrawer(GravityCompat.START) else binding.drawerLayout.openDrawer(
                GravityCompat.START
            )
            /////////////////////////////////
            binding.navView.bringToFront()

            binding.navView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        binding.drawerLayout.closeDrawers()
                        true
                    }
//                    R.id.history -> {
//                        startActivity(Intent(this,RecordActivity::class.java))
//                        binding.drawerLayout.closeDrawers()
//                        true
//                    }
                    R.id.more_apps -> {
                        val uri =
                            Uri.parse("https://play.google.com/store/apps/developer?id=Westminster+Pro+Apps")
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/developer?id=Westminster+Pro+Apps")
                                )
                            )
                        }
                        binding.drawerLayout.closeDrawers()
                        true
                    }
                    R.id.share -> {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey check out this app at: https://play.google.com/store/apps/details?id=" + applicationContext.packageName
                        )
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                        binding.drawerLayout.closeDrawers()
                        true
                    }
                    R.id.rate -> {
                        val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)
                                )
                            )
                        }
                        binding.drawerLayout.closeDrawers()
                        true
                    }
                    R.id.policy -> {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://westminsterproapps.blogspot.com/2022/04/privacy-policy.html"))
                        startActivity(browserIntent)
                        binding.drawerLayout.closeDrawers()
                        true
                    }
                    R.id.exit->{
                        binding.drawerLayout.closeDrawers()
                        val alertDialog = AlertDialog.Builder(this)
                        val customLayout: View = getLayoutInflater().inflate(R.layout.dialog, null)
                        alertDialog.setView(customLayout)
                        val alert = alertDialog.create()
                        alert.setCancelable(false)
                        alert.setCanceledOnTouchOutside(true)
                        val yesBtn: TextView = customLayout.findViewById(R.id.btn_yes)
                        val noBtn: TextView = customLayout.findViewById(R.id.btn_no)

                        noBtn.setOnClickListener {
                            alert.dismiss()
                        }
                        yesBtn.setOnClickListener {
                            finishAffinity()
                        }

                        alert.show()
                        true
                    }

                    else -> {
                        false
                    }
                }

            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 999 && resultCode == RESULT_OK) {
//            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
//            val r = RingtoneManager.getRingtone(applicationContext, uri)
//            r.play()
//        }
//    }

}