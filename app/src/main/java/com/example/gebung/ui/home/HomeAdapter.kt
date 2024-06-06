package com.example.gebung.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.ListTransactionBinding

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
                tvInfo.text = listTransaction.title
                tvDate.text = listTransaction.date
                tvMoney.text = listTransaction.nominal.toString()
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