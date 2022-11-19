package hu.bme.aut.android.hf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.hf.data.Transaction
import hu.bme.aut.android.hf.databinding.BalanceInfoListBinding
import java.text.FieldPosition

class TransactionAdapter(private val listener: TransactionClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    private val transactions = mutableListOf<Transaction>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionViewHolder(
        BalanceInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.binding.tvName.text = transaction.name
        holder.binding.tvDescription.text = transaction.description
        holder.binding.tvCategory.text = transaction.category.name
        if (transaction.income && !transaction.expense) {
            holder.binding.amount.text = "+${transaction.amount} Ft"
        } else if (!transaction.income && transaction.expense) {
            holder.binding.amount.text = "-${transaction.amount} Ft"
        } else {}

        holder.binding.delete.setOnClickListener {
            transactions.removeAt(position)
            listener.onTransactionDelete(transaction)
            notifyItemRemoved(position)
        }
    }

    interface TransactionClickListener {
        fun onTransactionChanged(transaction: Transaction)
        fun onTransactionDelete(transaction: Transaction)
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        transactions.remove(transaction)
        notifyDataSetChanged()
    }

    fun update(accountTransactions: List<Transaction>) {
        transactions.clear()
        transactions.addAll(accountTransactions)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(val binding: BalanceInfoListBinding) : RecyclerView.ViewHolder(binding.root)
}