package com.example.gebung.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gebung.R
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.ListHistoryTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val listHistoryTrans = ArrayList<Transaction>()

    fun getData(getList: List<Transaction>){
        listHistoryTrans.clear()
        listHistoryTrans.addAll(getList)
        notifyDataSetChanged()
    }
    class HistoryViewHolder(private val binding: ListHistoryTransactionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listHistory: Transaction){
            binding.apply {
                tvInfo.text = listHistory.description
                tvDate.text = listHistory.date
                val mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                val formattedNominal = mCurrencyFormat.format(listHistory.amount)
                tvMoney.text = formattedNominal

                val color = when(listHistory.type){
                    "Income" -> ContextCompat.getColor(binding.root.context, R.color.colorIncome)
                    "Expense" -> ContextCompat.getColor(binding.root.context, R.color.colorExpense)
                    else -> ContextCompat.getColor(binding.root.context, R.color.black)
                }
                tvMoney.setTextColor(color)

                val iconRes = when(listHistory.type){
                    "Income" -> R.drawable.ic_income
                    "Expense" -> R.drawable.ic_income
                    else -> R.drawable.ic_income
                }
                viewCircle.setImageResource(iconRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListHistoryTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = listHistoryTrans.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val list = listHistoryTrans[position]
        holder.bind(list)
    }
}