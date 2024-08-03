package org.hyperskill.simplebankmanager

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.Base64

class PayBillsFragment : Fragment() {

    private var billMap: Map<String, Triple<String, String, Double>> = emptyMap()
    private var balance: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val billInfoJson = it.getString("billMap")
            billMap = billInfoJson?.let { json ->
                deserializeMap(json) ?: defaultBillInfoMap
            } ?: defaultBillInfoMap
        }

        // Retrieve balance from arguments or some other source
        balance = arguments?.getString("balance")?.toDoubleOrNull() ?: 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pay_bills, container, false)

        val codeInputEditText = view.findViewById<EditText>(R.id.payBillsCodeInputEditText)
        val showBillInfoButton = view.findViewById<Button>(R.id.payBillsShowBillInfoButton)

        showBillInfoButton.setOnClickListener {
            val billCode = codeInputEditText.text.toString()
            handleBillInfo(billCode)
        }

        return view
    }

    private fun handleBillInfo(billCode: String) {
        val billInfo = billMap[billCode]

        if (billInfo == null) {
            showAlertDialog("Error", "Wrong code", "Ok")
        } else {
            val (billName, _, billAmount) = billInfo
            val message = "Name: $billName\nBillCode: $billCode\nAmount: $${"%.2f".format(billAmount)}"
            showConfirmationDialog(billName, billCode, billAmount, message)
        }
    }

    private fun showAlertDialog(title: String, message: String, positiveButton: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showConfirmationDialog(billName: String, billCode: String, billAmount: Double, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Bill info")
            .setMessage(message)
            .setPositiveButton("Confirm") { dialog, _ ->
                if (balance >= billAmount) {
                    balance -= billAmount
                    Toast.makeText(context, "Payment for bill $billName, was successful", Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    val bundle = Bundle().apply {
                        putString("balance", balance.toString())
                    }
//                    findNavController().navigate(R.id.action_payBillsFragment_to_userMenuFragment, bundle)
                    parentFragmentManager.setFragmentResult("balanceRequestKey", bundle)
                } else {
                    dialog.dismiss()
                    showAlertDialog("Error", "Not enough funds", "Ok")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deserializeMap(serialized: String): Map<String, Triple<String, String, Double>>? {
        val byteArray = Base64.getDecoder().decode(serialized)
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        return objectInputStream.readObject() as? Map<String, Triple<String, String, Double>>
    }

    private val defaultBillInfoMap = mapOf(
        "ELEC" to Triple("Electricity", "ELEC", 45.0),
        "GAS" to Triple("Gas", "GAS", 20.0),
        "WTR" to Triple("Water", "WTR", 25.5)
    )
}
