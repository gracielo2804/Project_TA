package com.gracielo.projectta.ui.setting

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.ActivityNotificationSettingBinding
import com.gracielo.projectta.notification.DailyReminder
import com.jakewharton.threetenabp.AndroidThreeTen
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationSettingActivity : AppCompatActivity() {
    lateinit var  binding : ActivityNotificationSettingBinding
    val daily= DailyReminder()
    var breakfastTime:String =""
    var lunchTime:String =""
    var dinnerTime:String =""
    var notificationstate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)

        val sharedPreference =  getSharedPreferences("Notification Time", Context.MODE_PRIVATE)
        breakfastTime = sharedPreference.getString("Breakfast","")!!
        var hourBreakfast = breakfastTime.split(":")[0].toInt()
        var minBreakfast = breakfastTime.split(":")[0].toInt()
        lunchTime = sharedPreference.getString("Lunch","")!!
        var hourLunch = lunchTime.split(":")[0].toInt()
        var minLunch = lunchTime.split(":")[0].toInt()
        dinnerTime = sharedPreference.getString("Dinner","")!!
        var hourdinner = dinnerTime.split(":")[0].toInt()
        var mindinner = dinnerTime.split(":")[0].toInt()
        notificationstate = sharedPreference.getBoolean("Notification",true)
        binding.txtBreakFastTime.text = breakfastTime
        binding.txtlunchTime.text = lunchTime
        binding.txtdinnerTime.text = dinnerTime
        binding.switch1.isChecked =notificationstate

        binding.switch1.setOnCheckedChangeListener{buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(this,"Reminder Activated", Toast.LENGTH_SHORT).show()
                daily.setDailyReminder(this)
                var editor = sharedPreference.edit()
                editor.putBoolean("Notification",true)
                editor.commit()
            } else{
                Toast.makeText(this,"Reminder Canceled", Toast.LENGTH_SHORT).show()
                daily.cancelAlarm(this)
                var editor = sharedPreference.edit()
                editor.putBoolean("Notification",false)
                editor.commit()
            }
        }


//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.preferences_notification, SettingsFragment())
//            .commit()


        binding.btnChangeBreakfast.setOnClickListener {

            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                if(hour>hourLunch||hour>hourdinner||(hour>=hourLunch && minute>minLunch)||(hour>=hourdinner && minute>mindinner)){
                    Toast.makeText(this,"Breakfast time cannot after lunch time or dinner time",Toast.LENGTH_SHORT).show()
                }
                else{
                    hourBreakfast = hour
                    minBreakfast = minute
                    val hasil = SimpleDateFormat("HH:mm").format(cal.time)
                    binding.txtBreakFastTime.text = hasil
                    breakfastTime = hasil
                    var editor = sharedPreference.edit()
                    editor.remove("Breakfast")
                    editor.putString("Breakfast",hasil)
                    editor.commit()
                    daily.setDailyReminder(this)
                    Toast.makeText(this,"Breakfast time changed",Toast.LENGTH_SHORT).show()
                }

            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        binding.btnChangeLunch.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                if(hour<hourBreakfast||hour>hourdinner||(hour==hourBreakfast && minute<=minBreakfast)||(hour>=hourdinner && minute>mindinner)){
                    Toast.makeText(this,"Lunch time cannot after dinner time or before breakfast time",Toast.LENGTH_SHORT).show()
                }
                else{
                    hourLunch = hour
                    minLunch = minute
                    val hasil = SimpleDateFormat("HH:mm").format(cal.time)
                    binding.txtlunchTime.text = hasil
                    lunchTime = hasil
                    var editor = sharedPreference.edit()
                    editor.remove("Lunch")
                    editor.putString("Lunch",hasil)
                    editor.commit()
                    daily.setDailyReminder(this)
                    Toast.makeText(this,"Lunch time changed",Toast.LENGTH_SHORT).show()
                }
               

            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        binding.btnChangeDinner.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                if(hour<hourLunch||hour<hourBreakfast||(hour<=hourLunch && minute<minLunch)||(hour<=hourBreakfast && minute<minBreakfast)){
                    Toast.makeText(this,"Dinner time cannot before lunch time or dinner time",Toast.LENGTH_SHORT).show()
                }
                else{

                    hourdinner = hour
                    mindinner = minute
                    val hasil = SimpleDateFormat("HH:mm").format(cal.time)
                    binding.txtdinnerTime.text = hasil
                    dinnerTime = hasil
                    var editor = sharedPreference.edit()
                    editor.remove("Dinner")
                    editor.putString("Dinner",hasil)
                    editor.commit()
                    daily.setDailyReminder(this)
                    Toast.makeText(this,"Dinner time changed",Toast.LENGTH_SHORT).show()
                }


            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }


    }
    class SettingsFragment: PreferenceFragmentCompat() {
        val daily= DailyReminder()
        //        lateinit var dailyReminder:PeriodicWorkRequest;
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if (!prefNotification.isChecked){
                    Toast.makeText(context,"Reminder Activated", Toast.LENGTH_SHORT).show()
                    daily.setDailyReminder(requireContext())
                } else{
                    Toast.makeText(context,"Reminder Canceled", Toast.LENGTH_SHORT).show()
                    daily.cancelAlarm(requireContext())
                }

                true
            }
        }
    }
}