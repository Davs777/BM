package org.hyperskill.simplebankmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.text.DecimalFormat
import java.text.NumberFormat

class TransferFundsFragment : Fragment() {

    private var balance: Double = 0.0
    private var billMap: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            balance = it.getString("balance")?.toDoubleOrNull() ?: 0.0
            billMap = it.getString("billMap")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_funds, container, false)

        val accountEditText = view.findViewById<EditText>(R.id.transferFundsAccountEditText)
        val amountEditText = view.findViewById<EditText>(R.id.transferFundsAmountEditText)
        val transferButton = view.findViewById<Button>(R.id.transferFundsButton)

        transferButton.setOnClickListener {
            val account = accountEditText.text.toString().trim()
            val amountString = amountEditText.text.toString().trim()

            var valid = true

            if (!account.matches(Regex("^(sa|ca)\\d{4}$"))) {
                accountEditText.error = "Invalid account number"
                valid = false
            } else {
                accountEditText.error = null
            }

            val amount = amountString.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                amountEditText.error = "Invalid amount"
                valid = false
            } else {
                amountEditText.error = null
            }

            if (valid) {
                if (amount != null) {
                    if (amount > balance) {
                        val formattedAmount = formatAmount(amount)
                        Toast.makeText(context, "Not enough funds to transfer $$formattedAmount", Toast.LENGTH_LONG).show()
                        // Do not update the balance or navigate back here
                    } else {
                        val formattedAmount = formatAmount(amount)
                        val formattedBalance = formatAmount(balance - amount)
                        Toast.makeText(context, "Transferred $$formattedAmount to account $account", Toast.LENGTH_LONG).show()

                        // Navigate back to UserMenuFragment with updated balance
                        val bundle = Bundle().apply {
                            putString("balance", (balance - amount).toString())
                            putString("billMap", billMap)
                        }
                        findNavController().navigate(R.id.action_transferFundsFragment_to_userMenuFragment, bundle)
                    }
                }
            }

        }

        return view
    }

    private fun formatAmount(amount: Double): String {
        val numberFormat: NumberFormat = DecimalFormat("#,##0.00")
        return numberFormat.format(amount)
    }
}
