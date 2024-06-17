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
    private val PREFS_NAME = "MyPrefs"
    private val SWITCH_STATUS_KEY = "switchStatus"
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
                Toast.makeText(requireContext(), "Reminder Enabled", Toast.LENGTH_SHORT).show()
            }else{
                cancelAlarm()
                Toast.makeText(requireContext(), "Reminder Canceled", Toast.LENGTH_SHORT).show()
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
    }

    private fun cancelAlarm(){
        alarmManager.cancel(pendingIntent)
    }

    private fun saveSwitchStatus(enabled: Boolean){
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(SWITCH_STATUS_KEY, enabled).apply()
    }

    private fun loadSwitchStatus(): Boolean {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(SWITCH_STATUS_KEY, false)
    }

}