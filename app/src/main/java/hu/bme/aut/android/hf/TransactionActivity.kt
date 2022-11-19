package hu.bme.aut.android.hf

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import hu.bme.aut.android.hf.adapter.AccountAdapter
import hu.bme.aut.android.hf.adapter.TransactionAdapter
import hu.bme.aut.android.hf.data.*
import hu.bme.aut.android.hf.databinding.BalanceInfoBinding
import hu.bme.aut.android.hf.fragments.NewAccountBalanceDialogFragment
import hu.bme.aut.android.hf.fragments.NewTransactionDialogFragment
import kotlin.concurrent.thread

class TransactionActivity : AppCompatActivity(), TransactionAdapter.TransactionClickListener,
    NewTransactionDialogFragment.NewTransactionDialogListener {

    lateinit var binding: BalanceInfoBinding

    private lateinit var transactionDatabase: TransactionDatabase
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var accountListDatabase: AccountListDatabase
    private lateinit var editedDatabase: AccountListDatabase
    private lateinit var accountAdapter: AccountAdapter
    private var name: String? = null
    private var balance: String? = null
    private var ID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BalanceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        name = intent.getStringExtra("AccountName")
        balance = intent.getStringExtra("AccountBalance")
        binding.name.text = name
        binding.balance.text = "${ balance } Ft"

        transactionDatabase = TransactionDatabase.getDatabase(applicationContext)


        binding.add.setOnClickListener {
            NewTransactionDialogFragment().show(
                supportFragmentManager,
                NewTransactionDialogFragment.TAG
            )
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        transactionAdapter = TransactionAdapter(this)
        binding.listMain.layoutManager = LinearLayoutManager(this)
        binding.listMain.adapter = transactionAdapter
        loadTransactionsInBackground()
    }


    private fun loadTransactionsInBackground() {
        thread {
            val transactions = transactionDatabase.transactionDao().getAll()
            runOnUiThread {
                transactionAdapter.update(transactions)
            }
        }
    }

    override fun onTransactionChanged(transaction: Transaction) {
        thread {
            transactionDatabase.transactionDao().update(transaction)
            Log.d("TransactionActivity", "Transaction update was successful")
        }
    }

    override fun onTransactionDelete(transaction: Transaction) {
        thread {
            accountListDatabase = AccountListDatabase.getDatabase(applicationContext)
            ID = intent.getStringExtra("AccountId")?.toLong()
            var idx = 0
            for (i in accountListDatabase.accountBalanceDao().getAll()) {
                if (i.id == ID) {
                    break
                }
                idx++
            }

            var newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance
            if (transaction.income) {
                newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance - transaction.amount
            } else if (transaction.expense) {
                newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance + transaction.amount
            }

            accountListDatabase.accountBalanceDao().update(AccountBalance(
                accountListDatabase.accountBalanceDao().getAll()[idx].id,
                accountListDatabase.accountBalanceDao().getAll()[idx].name,
                newBal))

            transactionDatabase.transactionDao().deleteTransaction(transaction)
            runOnUiThread {
                transactionAdapter.deleteTransaction(transaction)
            }
            Log.d("TransactionActivity", "Transaction update was successful")
        }
    }

    override fun onTransactionCreate(newTransaction: Transaction) {
        thread {
            accountListDatabase = AccountListDatabase.getDatabase(applicationContext)
            ID = intent.getStringExtra("AccountId")?.toLong()
            var idx = 0
            for (i in accountListDatabase.accountBalanceDao().getAll()) {
                if (i.id == ID) {
                    break
                }
                idx++
            }

            var newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance
            if (newTransaction.income) {
                newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance + newTransaction.amount
            } else if (newTransaction.expense) {
                newBal = accountListDatabase.accountBalanceDao().getAll()[idx].balance - newTransaction.amount
            }

            accountListDatabase.accountBalanceDao().update(AccountBalance(
                accountListDatabase.accountBalanceDao().getAll()[idx].id,
                accountListDatabase.accountBalanceDao().getAll()[idx].name,
                newBal))
            val insertId = transactionDatabase.transactionDao().insert(newTransaction)
            newTransaction.id = insertId
            runOnUiThread {
                transactionAdapter.addTransaction(newTransaction)
            }
        }
    }

    companion object {
        const val TAG = "Transaction"
    }
}
