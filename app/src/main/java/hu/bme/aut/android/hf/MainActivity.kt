package hu.bme.aut.android.hf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.hf.adapter.AccountAdapter
import hu.bme.aut.android.hf.data.AccountBalance
import hu.bme.aut.android.hf.data.AccountListDatabase
import hu.bme.aut.android.hf.data.Transaction
import hu.bme.aut.android.hf.databinding.AccountBalanceListBinding
import hu.bme.aut.android.hf.databinding.ActivityMainBinding
import hu.bme.aut.android.hf.fragments.NewAccountBalanceDialogFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), AccountAdapter.AccountBalanceClickListener,
    NewAccountBalanceDialogFragment.NewAccountBalanceDialogListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var database: AccountListDatabase
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

    private fun initRecyclerView() {
        adapter = AccountAdapter(this)
        binding.listMain.layoutManager = LinearLayoutManager(this)
        binding.listMain.adapter = adapter
        loadBalancesInBackground()
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
            runOnUiThread {
                adapter.deleteBalance(balance)
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
}