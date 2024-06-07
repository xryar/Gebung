package com.example.gebung.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.ListHistoryTransactionBinding

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
                tvInfo.text = listHistory.title
                tvDate.text = listHistory.date
                tvMoney.text = listHistory.nominal.toString()
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