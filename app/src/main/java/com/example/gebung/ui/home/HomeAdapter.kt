package com.example.gebung.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gebung.R
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.ListTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private val listTrans = ArrayList<Transaction>()

    fun getData(getList: List<Transaction>){
        listTrans.clear()
        listTrans.addAll(getList)
        notifyDataSetChanged()
    }
    class HomeViewHolder(private val binding:ListTransactionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listTransaction: Transaction){
            binding.apply {
                tvInfo.text = listTransaction.description
                tvDate.text = listTransaction.date
                val mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                val formattedNominal = mCurrencyFormat.format(listTransaction.amount)
                tvMoney.text = formattedNominal

                val color = when(listTransaction.type){
                    "Income" -> ContextCompat.getColor(binding.root.context, R.color.colorIncome)
                    "Expense" -> ContextCompat.getColor(binding.root.context, R.color.colorExpense)
                    else -> ContextCompat.getColor(binding.root.context, R.color.black)
                }
                tvMoney.setTextColor(color)

                val iconRes = when (listTransaction.type) {
                    "Income" -> R.drawable.ic_income
                    "Expense" -> R.drawable.ic_expense
                    else -> R.drawable.ic_expense // Add a default image if necessary
                }
                imgInfo.setImageResource(iconRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = listTrans.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val list = listTrans[position]
        holder.bind(list)
    }
}