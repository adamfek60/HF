package hu.bme.aut.android.hf.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.hf.data.AccountBalance
import hu.bme.aut.android.hf.databinding.DialogNewBalanceBinding
import java.lang.RuntimeException

class NewAccountBalanceDialogFragment : DialogFragment() {
    interface NewAccountBalanceDialogListener {
        fun onAccountBalanceCreate(newBalance: AccountBalance)
    }

    private lateinit var listener: NewAccountBalanceDialogListener

    private lateinit var binding: DialogNewBalanceBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewAccountBalanceDialogListener
            ?: throw RuntimeException("Activity must implement the NewAccountBalanceDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewBalanceBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("New Account Balance")
            .setView(binding.root)
            .setPositiveButton("OK") { dialogInterface, i ->
                if (isValid()) {
                    listener.onAccountBalanceCreate(getAccountBalance())
                } else {
                    Toast.makeText(context, "Add name!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun isValid() = binding.etName.text.isNotEmpty()

    private fun getAccountBalance() = AccountBalance(
        name = binding.etName.text.toString(),
        balance = binding.etBalance.text.toString().toIntOrNull() ?: 0,
    )

    companion object {
        const val TAG = "NewAccountBalanceDialogFragment"
    }
}