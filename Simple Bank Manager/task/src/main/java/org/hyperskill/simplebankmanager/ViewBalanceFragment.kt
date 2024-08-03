package org.hyperskill.simplebankmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ViewBalanceFragment : Fragment() {

    private var balance: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            balance = it.getString("balance")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_balance, container, false)

        val balanceTextView = view.findViewById<TextView>(R.id.viewBalanceAmountTextView)

        balance?.let {
            try {
                // Convert balance to a Double and format it
                val formattedBalance = String.format("$%.2f", it.toDouble())
                balanceTextView.text = formattedBalance
            } catch (e: NumberFormatException) {
                // Handle the case where balance is not a valid number
                balanceTextView.text = "Invalid balance"
            }
        }

        return view
    }
}
