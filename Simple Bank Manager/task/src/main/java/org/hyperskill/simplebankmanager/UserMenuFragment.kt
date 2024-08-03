package org.hyperskill.simplebankmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class UserMenuFragment : Fragment() {

    private var username: String? = null
    private var balance: String? = null
    private var exchangeMap: String? = null
    private var billMap: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username")
            balance = it.getString("balance")
            exchangeMap = it.getString("exchangeMap")
            billMap = it.getString("billMap")
        }
        println(billMap )
        println(username )

    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_menu, container, false)

        val welcomeTextView = view.findViewById<TextView>(R.id.userMenuWelcomeTextView)
        welcomeTextView.text = "Welcome $username"

        val viewBalanceButton = view.findViewById<Button>(R.id.userMenuViewBalanceButton)
        viewBalanceButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("balance", balance)
            }
            findNavController().navigate(R.id.action_userMenuFragment_to_viewBalanceFragment, bundle)
        }

        val transferFundsButton = view.findViewById<Button>(R.id.userMenuTransferFundsButton)
        transferFundsButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("balance", balance)
                putString("billMap", billMap)
            }
            findNavController().navigate(R.id.action_userMenuFragment_to_transferFundsFragment, bundle)
        }

        val exchangeCalculatorButton = view.findViewById<Button>(R.id.userMenuExchangeCalculatorButton)
        exchangeCalculatorButton.setOnClickListener {

            val bundle = Bundle().apply {
                putString("exchangeMap", exchangeMap)
            }


            findNavController().navigate(R.id.action_userMenuFragment_to_calculateExchangeFragment, bundle)
        }

        val payBillsButton = view.findViewById<Button>(R.id.userMenuPayBillsButton)
        payBillsButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("balance", balance)
                putString("billMap", billMap)
            }
            findNavController().navigate(R.id.action_userMenuFragment_to_payBillsFragment, bundle)
        }




        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("balanceRequestKey", viewLifecycleOwner) { _, bundle ->
            val updatedBalance = bundle.getString("balance")
            balance = updatedBalance ?: balance
        }
    }


}
