package com.example.gebung.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gebung.R
import com.example.gebung.databinding.FragmentHomeBinding
import com.example.gebung.ui.customdialog.CustomDialogFragment
import com.example.gebung.ui.customdialog.LimitDialogFragment
import com.example.gebung.ui.history.HistoryActivity
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory

class HomeFragment : Fragment(), LimitDialogFragment.LimitSetListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: HomeAdapter

    private var savedLimit: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory(requireActivity().application)
        // Gunakan ViewModelFactory saat membuat instance dari TransactionViewModel
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)
        adapter = HomeAdapter()
        showRecyclerView()
        showViewModel()
        actionListener()
        observeTotalExpense()
        loadSavedLimit()

        return binding.root
    }


    private fun updateProgressBar() {
        val totalExpense = viewModel.totalExpense.value ?: 0
        if (savedLimit > 0){
            binding.horizontalProgressBar.max = savedLimit
            binding.horizontalProgressBar.progress = totalExpense

            val percentage = (totalExpense.toFloat() / savedLimit * 100).toInt()
            binding.tvPercentageInfo.text = "$percentage%"

            if (totalExpense > savedLimit && !loadNotificationStatus()){
                showLimitNotification()
                saveNotificationStatus(true)
            }
        }else{
            binding.tvPercentageInfo.text = "0%"
        }
    }

    private fun showLimitNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "expense_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Expense Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notifications for expense limit"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Limit Exceeded")
            .setContentText("Oi oi oi")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onLimitSet(limit: Int){
        binding.tvMoney.text = limit.toString()
        saveLimit(limit)
        updateProgressBar()
        saveNotificationStatus(false) //Reset
    }

    private fun saveLimit(limit: Int){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putInt("spending_limit", limit)
            apply()
        }
        savedLimit = limit
    }

    private fun loadSavedLimit() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val savedLimit = sharedPref?.getInt("spending_limit", 0)
        if (savedLimit != null && savedLimit != 0){
            binding.tvMoney.text = savedLimit.toString()
            this.savedLimit = savedLimit
            updateProgressBar()
        }
    }

    private fun saveNotificationStatus(status: Boolean){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putBoolean("notification_sent", status)
            apply()
        }
    }

    private fun loadNotificationStatus(): Boolean{
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return sharedPref?.getBoolean("notification_sent", false) ?: false
    }
    private fun observeTotalExpense() {
        viewModel.totalExpense.observe(viewLifecycleOwner){totalExpense->
            binding.tvTotalExpense.text = totalExpense?.toString() ?: "0"
            updateProgressBar()
        }
    }

    private fun showViewModel() {
        viewModel.getLastTransaction().observe(viewLifecycleOwner){
            adapter.getData(it)
        }
    }

    private fun showRecyclerView() {
        binding.apply {
            rvListTransaction.layoutManager = LinearLayoutManager(activity)
            rvListTransaction.setHasFixedSize(true)
            rvListTransaction.adapter = adapter
        }
    }

    private fun actionListener() {

        binding.btnShop.setOnClickListener {
            showDialog(getString(R.string.shop))
        }

        binding.btnFood.setOnClickListener {
            showDialog(getString(R.string.food))
        }

        binding.btnTransport.setOnClickListener {
            showDialog(getString(R.string.transport))
        }

        binding.btnHealth.setOnClickListener {
            showDialog(getString(R.string.health))
        }

        binding.btnOther.setOnClickListener {
            showDialog(getString(R.string.other))
        }

        binding.tvHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLimit.setOnClickListener {
            val dialog = LimitDialogFragment()
            dialog.setLimitListener(this)
            dialog.show(requireActivity().supportFragmentManager, "LimitDialog")
        }

    }

    private fun showDialog(category: String) {
        val dialog = CustomDialogFragment.newInstance(category)

        dialog.show(requireActivity().supportFragmentManager, "CustomDialog")

    }
}