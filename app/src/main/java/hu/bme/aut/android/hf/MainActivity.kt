package hu.bme.aut.android.hf

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.hf.adapter.AccountAdapter
import hu.bme.aut.android.hf.data.AccountBalance
import hu.bme.aut.android.hf.data.AccountListDatabase
import hu.bme.aut.android.hf.data.TransactionDatabase
import hu.bme.aut.android.hf.databinding.ActivityMainBinding
import hu.bme.aut.android.hf.fragments.NewAccountBalanceDialogFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), AccountAdapter.AccountBalanceClickListener,
    NewAccountBalanceDialogFragment.NewAccountBalanceDialogListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var database: AccountListDatabase
    private lateinit var transactionDatabase: TransactionDatabase
    private lateinit var adapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        database = AccountListDatabase.getDatabase(applicationContext)

        thread {
            val sum = database.accountBalanceDao().getAll().size

            if (sum > 0) {
                runOnUiThread {
                    binding.add.setEnabled(false)
                }
            } else {
                runOnUiThread{
                    binding.add.setEnabled(true)
                }
            }
        }

        binding.add.setOnClickListener {
            NewAccountBalanceDialogFragment().show(
                supportFragmentManager,
                NewAccountBalanceDialogFragment.TAG
            )
        }
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = AccountAdapter(this)
        binding.listMain.layoutManager = LinearLayoutManager(this)
        binding.listMain.adapter = adapter
        loadBalancesInBackground()
        loadChart()
    }

    private fun loadBalancesInBackground() {
        thread {
            val balances = database.accountBalanceDao().getAll()
            runOnUiThread {
                adapter.update(balances)
            }
        }
    }

    override fun onBalanceChanged(balance: AccountBalance) {
        thread {
            database.accountBalanceDao().update(balance)
            Log.d("MainActivity", "AccountBalance update was successful")
        }
    }

    override fun onBalanceDelete(balance: AccountBalance) {
        thread {
            database.accountBalanceDao().deleteBalance(balance)
            deleteDatabase("transaction")
            runOnUiThread {
                adapter.deleteBalance(balance)
                loadChart()
            }
            Log.d("MainActivity", "AccountBalance update was successful")
            val sum = database.accountBalanceDao().getAll().size
            if (sum > 0) {
                runOnUiThread {
                    binding.add.setEnabled(false)
                }
            } else {
                runOnUiThread{
                    binding.add.setEnabled(true)
                }
            }
        }
    }

    override fun onAccountBalanceCreate(newBalance: AccountBalance) {
        thread {
            val insertId = database.accountBalanceDao().insert(newBalance)
            newBalance.id = insertId
            runOnUiThread {
                adapter.addBalance(newBalance)
                loadChart()
            }
            val sum = database.accountBalanceDao().getAll().size
            if (sum > 0) {
                runOnUiThread {
                    binding.add.setEnabled(false)
                }
            } else {
                runOnUiThread{
                    binding.add.setEnabled(true)
                }
            }
        }
    }

    override fun onBalanceSelected(balance: Int, name: String, id: Long?) {
        val showTransactionIntent = Intent()
        showTransactionIntent.setClass(this@MainActivity, TransactionActivity::class.java)
        showTransactionIntent.putExtra("AccountBalance", balance.toString())
            .putExtra("AccountName", name)
            .putExtra("AccountId", id.toString())
        startActivity(showTransactionIntent)
    }

    private fun loadChart() {
        thread {
            transactionDatabase = TransactionDatabase.getDatabase(applicationContext)
            database = AccountListDatabase.getDatabase(applicationContext)
            var income = 0
            var expense = 0
            var default = 0
            default = if (database.accountBalanceDao().getAll().isEmpty()) {
                0
            } else {
                database.accountBalanceDao().getAll()[0].balance
            }


            for (i in transactionDatabase.transactionDao().getAll()) {
                if (i.income) {
                    income += i.amount
                } else if (i.expense) {
                    expense += i.amount
                }
            }

            val entries = listOf(
                PieEntry(income.toFloat(), "Income"),
                PieEntry((default + expense - income).toFloat(),
                    "Default Balance"),
                PieEntry(expense.toFloat(), "Expense")
            )

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()


            val data = PieData(dataSet)
            data.setValueTextSize(20f)
            binding.chart.data = data
            binding.chart.invalidate()
        }
    }
}