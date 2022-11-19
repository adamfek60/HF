package hu.bme.aut.android.hf.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.hf.R
import hu.bme.aut.android.hf.data.Transaction
import hu.bme.aut.android.hf.databinding.DialogNewTransactionBinding
import java.lang.RuntimeException

class NewTransactionDialogFragment : DialogFragment() {
    interface NewTransactionDialogListener {
        fun onTransactionCreate(newTransaction: Transaction)
    }

    private lateinit var listener: NewTransactionDialogListener

    private lateinit var binding: DialogNewTransactionBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewTransactionDialogListener
            ?: throw RuntimeException("Activity must implement the NewTransactionDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewTransactionBinding.inflate(LayoutInflater.from(context))
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.category_items)
        )

        return AlertDialog.Builder(requireContext())
            .setTitle("New Transaction")
            .setView(binding.root)
            .setPositiveButton("OK") { dialogInterface, i ->
                if (isValid()) {
                    listener.onTransactionCreate(getTransaction())
                } else {
                    Toast.makeText(context, "Add amount!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun isValid() = binding.amount.text.isNotEmpty()

    private fun getTransaction() = Transaction(
        name = binding.etName.text.toString(),
        description = binding.etDescription.text.toString(),
        income = binding.income.isChecked,
        expense = binding.expense.isChecked,
        category = Transaction.Category.getByOrdinal(binding.spCategory.selectedItemPosition)
            ?: Transaction.Category.OTHER,
        amount = binding.amount.text.toString().toIntOrNull() ?: 0
    )

    companion object {
        const val TAG = "NewTransactionDialogFragment"
    }

}