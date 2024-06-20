package com.example.gebung.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gebung.R
import com.example.gebung.databinding.FragmentHomeBinding
import com.example.gebung.ui.customdialog.CustomDialogFragment
import com.example.gebung.ui.customdialog.LimitDialogFragment
import com.example.gebung.ui.customdialog.NotificationDialogFragment
import com.example.gebung.ui.history.HistoryActivity
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.CirclePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), LimitDialogFragment.LimitSetListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var auth: FirebaseAuth
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    private var savedLimit: Int = 0
    private var mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private var currentPrompt: MaterialTapTargetPrompt ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory(requireActivity().application)

        viewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        adapter = HomeAdapter()
        showRecyclerView()
        showViewModel()
        actionListener()
        observeTotalExpense()
        loadSavedLimit()
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        binding.accountName.text = firebaseUser?.displayName.toString()

        showButtonPromptsSequentially()
        return binding.root
    }

    private fun showButtonPromptsSequentially() {
        val prefManager = activity?.getPreferences(Context.MODE_PRIVATE)
        if (prefManager != null) {
            if (!prefManager.getBoolean("didShowPrompt", false)) {
                val prefEditor = prefManager.edit()
                prefEditor.putBoolean("didShowPrompt", true).apply()
                val views = listOf(binding.ibNotification, binding.btnLimit, binding.btnShop, binding.btnOther)
                val titles = listOf("Notification", "Set limit", "Shop", "Other")
                val descriptions = listOf("Turn on your daily reminder", "Set your limit", "Record your expenses by category", "Record expenses and income with other categories")
                showNextButtonPrompt(views, titles, descriptions, 0)
            }
        }
    }

    private fun showNextButtonPrompt(views: List<View>, titles: List<String>, descriptions: List<String>, index: Int) {
        currentPrompt?.dismiss()
        if (index < views.size) {
            val view = views[index]
            val title = titles[index]
            val description = descriptions[index]
            MaterialTapTargetPrompt.Builder(activity as AppCompatActivity)
                .setTarget(view)
                .setPrimaryText(title)
                .setSecondaryText(description)
                .setBackButtonDismissEnabled(true)
                .setPromptBackground(CirclePromptBackground())
                .setPromptFocal(CirclePromptFocal())
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                        state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED ||
                        state == MaterialTapTargetPrompt.STATE_BACK_BUTTON_PRESSED) {
                        prompt.dismiss()
                    }
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED ||
                        state == MaterialTapTargetPrompt.STATE_FINISHED ||
                        state == MaterialTapTargetPrompt.STATE_FINISHING){
                        currentPrompt = null
                        view.postDelayed({
                            showNextButtonPrompt(views, titles, descriptions, index + 1)
                        },  100)
                    }
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSavedLimit()
        observeTotalExpense()
        updateCircularProgressBar()
    }


    private fun updateCircularProgressBar(){
        val count = SharedPreferencesHelper.getTransactionDatesInNext7Days(requireContext()).size
        Log.d("HomeFragment", "Current week transaction count: $count")
        val progress = (count / 7.0) * 100
        binding.circularProgressBar.progress = progress.toInt()
        binding.progressText.text = "$count of 7"

        if (count >= 7){
            SharedPreferencesHelper.resetTransactionDates(requireContext())
            binding.circularProgressBar.progress = 0
            binding.progressText.text = "0 of 7"
            Log.d("HomeFragment", "Transaction dates reset after reaching 7")
        }
    }


    private fun updateProgressBar(totalExpense: Int) {
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
            .setContentTitle("You reached your expense limit!")
            .setContentText("Your transaction has exceeded the limit you set, please set over your expense limit to maintain your stability")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onLimitSet(limit: Int){
        val currentMonth = getCurrentMonthExpense()
        val formattedLimit = mCurrencyFormat.format(limit)
        binding.tvMoney.text = formattedLimit
        saveLimit(limit)
        updateProgressBar(viewModel.updateTotalExpense(currentMonth).value ?: 0)
        saveNotificationStatus(false) //Reset
    }

    private fun saveLimit(limit: Int){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putInt("spending_limit", limit)
            putInt("last_reset_month", getCurrentMonth())
            apply()
        }
        savedLimit = limit
    }

    private fun loadSavedLimit() {
        val currentMonthExpense = getCurrentMonthExpense()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val currentMonth = getCurrentMonth()
        val lastRestMonth = sharedPref?.getInt("last_reset_month", -1)

        if (lastRestMonth != currentMonth){
            saveLimit(0)
        }

        val savedLimit = sharedPref?.getInt("spending_limit", 0)
        if (savedLimit != null && savedLimit != 0){
            val formattedLimit = mCurrencyFormat.format(savedLimit)
            binding.tvMoney.text = formattedLimit
            this.savedLimit = savedLimit
            updateProgressBar(viewModel.updateTotalExpense(currentMonthExpense).value ?: 0)
        }
    }

    private fun getCurrentMonth(): Int{
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH)
    }

    private fun getCurrentMonthExpense(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return dateFormat.format(Date())
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
        val currentMonth = getCurrentMonthExpense()
        viewModel.updateTotalExpense(currentMonth).observe(viewLifecycleOwner){ totalExpense ->
            Log.d("FilterDataByMonth", "Total Expense: $totalExpense")
            val formattedExpense = mCurrencyFormat.format(totalExpense ?: 0)
            binding.tvTotalExpense.text = formattedExpense
            updateProgressBar(totalExpense ?: 0)
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
            showDialog(getString(R.string.shop), false)
        }

        binding.btnFood.setOnClickListener {
            showDialog(getString(R.string.food), false)
        }

        binding.btnTransport.setOnClickListener {
            showDialog(getString(R.string.transport), false)
        }

        binding.btnHealth.setOnClickListener {
            showDialog(getString(R.string.health), false)
        }

        binding.btnOther.setOnClickListener {
            showDialog(getString(R.string.other), true)
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

        binding.ibNotification.setOnClickListener {
            showNotificationDialog()
        }

    }

    private fun showNotificationDialog() {
        val dialog = NotificationDialogFragment()
        dialog.show(parentFragmentManager, "NotificationDialogFragment")
    }

    private fun showDialog(category: String, isCategoryEditable: Boolean) {
        val dialog = CustomDialogFragment.newInstance(category, isCategoryEditable)
        dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
    }
}