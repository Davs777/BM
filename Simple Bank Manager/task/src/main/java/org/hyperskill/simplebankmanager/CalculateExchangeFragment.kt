package org.hyperskill.simplebankmanager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class CalculateExchangeFragment : Fragment() {

    private var exchangeMap: Map<String, Map<String, Double>> = emptyMap()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the exchange map with the default values


        arguments?.let {
            val exchangeMapString = it.getString("exchangeMap")
            exchangeMap = exchangeMapString?.let { serialized ->
                deserializeMap(serialized) ?: defaultExchangeMap()
            } ?: defaultExchangeMap()
        }

        println("Exchange Map: $exchangeMap")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculate_exchange, container, false)

        val fromSpinner = view.findViewById<Spinner>(R.id.calculateExchangeFromSpinner)
        val toSpinner = view.findViewById<Spinner>(R.id.calculateExchangeToSpinner)
        val amountEditText = view.findViewById<EditText>(R.id.calculateExchangeAmountEditText)
        val calculateButton = view.findViewById<Button>(R.id.calculateExchangeButton)
        val displayTextView = view.findViewById<TextView>(R.id.calculateExchangeDisplayTextView)

        // Set up spinners
        val currencies = listOf("EUR", "GBP", "USD")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val fromCurrency = parent.getItemAtPosition(position).toString()
                val toCurrency = toSpinner.selectedItem.toString()

                if (fromCurrency == toCurrency) {
                    Toast.makeText(context, "Cannot convert to same currency", Toast.LENGTH_LONG).show()
                    // Automatically select the next currency
                    val nextCurrency = currencies.firstOrNull { it != fromCurrency } ?: currencies[0]
                    toSpinner.setSelection(currencies.indexOf(nextCurrency))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val toCurrency = parent.getItemAtPosition(position).toString()
                val fromCurrency = fromSpinner.selectedItem.toString()

                if (fromCurrency == toCurrency) {
                    Toast.makeText(context, "Cannot convert to same currency", Toast.LENGTH_LONG).show()
                    // Automatically select the next currency
                    val nextCurrency = currencies.firstOrNull { it != toCurrency } ?: currencies[0]
                    fromSpinner.setSelection(currencies.indexOf(nextCurrency))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        calculateButton.setOnClickListener {
            val fromCurrency = fromSpinner.selectedItem.toString()
            val toCurrency = toSpinner.selectedItem.toString()
            val amountText = amountEditText.text.toString()

            if (fromCurrency == toCurrency) {
                Toast.makeText(context, "Cannot convert to same currency", Toast.LENGTH_LONG).show()
                // Automatically select the next currency
                val nextCurrency = currencies.firstOrNull { it != fromCurrency } ?: currencies[0]
                toSpinner.setSelection(currencies.indexOf(nextCurrency))
                return@setOnClickListener
            }

            if (amountText.isEmpty()) {
                Toast.makeText(context, "Enter amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Invalid amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val exchangeRate = exchangeMap[fromCurrency]?.get(toCurrency)
            println("Exchange rate for $fromCurrency to $toCurrency: $exchangeRate")

            if (exchangeRate != null) {
                val convertedAmount = amount * exchangeRate
                val formattedOriginalAmount = formatAmount(amount, fromCurrency)
                val formattedConvertedAmount = formatAmount(convertedAmount, toCurrency)
                displayTextView.text = "$formattedOriginalAmount = $formattedConvertedAmount"
            } else {
                Toast.makeText(context, "Exchange rate not available", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun formatAmount(amount: Double, currency: String): String {
        val format = when (currency) {
            "USD" -> "$%.2f"
            "EUR" -> "€%.2f"
            "GBP" -> "£%.2f"
            else -> "%.2f"
        }
        return format.format(amount)
    }

    private fun defaultExchangeMap() = mapOf(
        "EUR" to mapOf(
            "GBP" to 0.5,
            "USD" to 2.0
        ),
        "GBP" to mapOf(
            "EUR" to 2.0,
            "USD" to 4.0
        ),
        "USD" to mapOf(
            "EUR" to 0.5,
            "GBP" to 0.25
        )
    )
}
