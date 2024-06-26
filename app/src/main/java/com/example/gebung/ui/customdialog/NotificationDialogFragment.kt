package com.example.gebung.ui.customdialog

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.FragmentNotificationDialogBinding
import java.util.Calendar

class NotificationDialogFragment : DialogFragment() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var binding: FragmentNotificationDialogBinding
    private val prefsName = "MyPrefs"
    private val switchStatusKey = "switchStatus"
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = FragmentNotificationDialogBinding.inflate(inflater, null, false)

        setupView()
        setupAlarmManager()
        binding.switchNotification.isChecked = loadSwitchStatus()

        builder.setView(binding.root)
        return builder.create()
    }

    private fun setupView() {
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            saveSwitchStatus(isChecked)
            if (isChecked){
                scheduleAlarm()
            }else{
                cancelAlarm()
            }
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupAlarmManager(){
        val alarmIntent = Intent(requireActivity(), AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun scheduleAlarm(){
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(requireContext(), "Reminder Enabled", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(){
        alarmManager.cancel(pendingIntent)
        Toast.makeText(requireContext(), "Reminder Canceled", Toast.LENGTH_SHORT).show()
    }

    private fun saveSwitchStatus(enabled: Boolean){
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(switchStatusKey, enabled).apply()
    }

    private fun loadSwitchStatus(): Boolean {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean(switchStatusKey, false)
    }

}