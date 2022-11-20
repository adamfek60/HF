package hu.bme.aut.android.hf.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.hf.TransactionActivity
import hu.bme.aut.android.hf.data.AccountBalance
import hu.bme.aut.android.hf.databinding.AccountBalanceListBinding
import hu.bme.aut.android.hf.databinding.DialogNewBalanceBinding
import kotlin.concurrent.thread

class AccountAdapter(private val listener: AccountBalanceClickListener) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {
    private val balances = mutableListOf<AccountBalance>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccountViewHolder(
        AccountBalanceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val accountBalance = balances[position]
        holder.binding.name.text = accountBalance.name
        holder.binding.balance.text = "${ accountBalance.balance } Ft"
        if (accountBalance.balance < 0) {
            holder.binding.balance.setTextColor(Color.parseColor("#e74c3c"))
        } else if (accountBalance.balance > 0) {
            holder.binding.balance.setTextColor(Color.parseColor("#2ecc71"))
        }
        holder.binding.delete.setOnClickListener{
            balances.removeAt(position)
            listener.onBalanceDelete(accountBalance)
            notifyItemRemoved(position)
        }
        holder.binding.balance.setOnClickListener {
            listener.onBalanceSelected(accountBalance.balance, accountBalance.name, accountBalance.id)
        }
    }

    fun addBalance(balance: AccountBalance) {
        balances.add(balance)
        notifyItemInserted(balances.size - 1)
    }

    fun deleteBalance(balance: AccountBalance) {
        balances.remove(balance)
        notifyDataSetChanged()
    }

    fun update(accountBalances: List<AccountBalance>) {
        balances.clear()
        balances.addAll(accountBalances)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = balances.size

    interface AccountBalanceClickListener {
        fun onBalanceChanged(balance: AccountBalance)
        fun onBalanceDelete(balance: AccountBalance)
        fun onBalanceSelected(balance: Int, name: String, id: Long?)
    }

    inner class AccountViewHolder(var binding: AccountBalanceListBinding) : RecyclerView.ViewHolder(binding.root)

}